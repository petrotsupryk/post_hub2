package petro.post_hub2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.entity.RefreshToken;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.repository.RefreshTokenRepository;
import petro.post_hub2.service.RefreshTokenService;
import petro.post_hub2.utils.ApiUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateOrUpdateRefreshToken(User user) {
        return refreshTokenRepository.findByUserId(user.getId())
                .map(refreshToken -> {
                            refreshToken.setCreated(LocalDateTime.now());
                            refreshToken.setToken(ApiUtils.generatedUUID());
                            return refreshTokenRepository.save(refreshToken);
                        }
                )
                .orElseGet(() -> {
                    RefreshToken refreshToken = new RefreshToken();
                    refreshToken.setUser(user);
                    refreshToken.setCreated(LocalDateTime.now());
                    refreshToken.setToken(ApiUtils.generatedUUID());
                    return refreshTokenRepository.save(refreshToken);
                });
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new NotFoundException(ApiErrorMessage.REFRESH_TOKEN_NOT_FOUND_BY_TOKEN.getMessage()));
        refreshToken.setToken(ApiUtils.generatedUUID());
        refreshToken.setCreated(LocalDateTime.now());
        return refreshTokenRepository.save(refreshToken);
    }
}
