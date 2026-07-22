package petro.post_hub2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import petro.post_hub2.mapper.UserMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.UserProfileDto;
import petro.post_hub2.model.entity.RefreshToken;
import petro.post_hub2.model.entity.Role;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.enums.UserSystemRole;
import petro.post_hub2.model.exceptions.DataExistsException;
import petro.post_hub2.model.exceptions.InvalidDataException;
import petro.post_hub2.model.exceptions.InvalidPasswordException;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.model.request.LoginRequest;
import petro.post_hub2.model.request.RegistrationRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.repository.RoleRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.JwtTokenProvider;
import petro.post_hub2.security.validation.PasswordUtils;
import petro.post_hub2.service.AuthService;
import petro.post_hub2.service.RefreshTokenService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public CustomResponse<UserProfileDto> login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidDataException(ApiErrorMessage.INVALID_EMAIL_OR_PASSWORD.getMessage());
        }
        User user = userRepository.findByEmailAndDeletedFalse(loginRequest.getEmail())
                .orElseThrow(() ->
                        new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(loginRequest.getEmail())));
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generatedToken(user);
        UserProfileDto userProfileDto = userMapper.convertUserToUserProfileDto(user, token, refreshToken.getToken());
        return CustomResponse.successWithToken(userProfileDto);
    }

    @Override
    public CustomResponse<UserProfileDto> registration(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_EMAIL_ALREADY_EXISTS.getMessage(registrationRequest.getEmail()));
        }
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new DataExistsException(ApiErrorMessage.USER_WITH_THIS_USERNAME_ALREADY_EXISTS
                    .getMessage(registrationRequest.getUsername()));
        }
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new InvalidDataException(ApiErrorMessage.PASSWORD_DOES_NOT_MATCH.getMessage());
        }
        if (PasswordUtils.isNotValidPassword(registrationRequest.getPassword())) {
            throw new InvalidPasswordException(ApiErrorMessage.INVALID_PASSWORD.getMessage());
        }
        Role role = roleRepository.findByName(UserSystemRole.USER.getRole())
                .orElseThrow(() ->
                        new NotFoundException(ApiErrorMessage.ROLE_NOT_FOUND_BY_NAME
                                .getMessage(UserSystemRole.USER.getRole())));
        User user = userMapper.convertRegistrationRequestToUser(registrationRequest);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user = userRepository.save(user);

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generatedToken(user);
        UserProfileDto userProfileDto = userMapper.convertUserToUserProfileDto(user, token, refreshToken.getToken());
        return CustomResponse.successWithToken(userProfileDto);
    }

    @Override
    public CustomResponse<UserProfileDto> refreshToken(String refreshToken) {
        RefreshToken newRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);
        User user = newRefreshToken.getUser();
        String token = jwtTokenProvider.generatedToken(user);
        UserProfileDto userProfileDto = userMapper.convertUserToUserProfileDto(user, token, newRefreshToken.getToken());
        return CustomResponse.successWithToken(userProfileDto);
    }
}
