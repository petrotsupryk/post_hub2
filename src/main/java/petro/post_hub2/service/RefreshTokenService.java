package petro.post_hub2.service;

import petro.post_hub2.model.entity.RefreshToken;
import petro.post_hub2.model.entity.User;

public interface RefreshTokenService {
    RefreshToken generateOrUpdateRefreshToken(User user);
    RefreshToken validateRefreshToken(String token);
}
