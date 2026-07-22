package petro.post_hub2.service;

import petro.post_hub2.model.dto.UserProfileDto;
import petro.post_hub2.model.request.LoginRequest;
import petro.post_hub2.model.request.RegistrationRequest;
import petro.post_hub2.model.response.CustomResponse;

public interface AuthService {
    CustomResponse<UserProfileDto> login(LoginRequest loginRequest);

    CustomResponse<UserProfileDto> registration(RegistrationRequest registrationRequest);

    CustomResponse<UserProfileDto> refreshToken(String refreshToken);
}
