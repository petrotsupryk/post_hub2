package petro.post_hub2.security.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.enums.UserSystemRole;
import petro.post_hub2.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final UserRepository userRepository;

    public boolean isAdminOrSuperAdmin(int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getRoles().stream()
                .map(role -> UserSystemRole.fromName(role.getName()))
                .anyMatch(role -> role == UserSystemRole.ADMIN || role == UserSystemRole.SUPER_ADMIN);
    }

    public void validateAdminOrOwnerAccess(int ownerId, int currentUserId){
        if(ownerId!=currentUserId && !isAdminOrSuperAdmin(currentUserId)){
            throw new AccessDeniedException(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage());
        }
    }
}
