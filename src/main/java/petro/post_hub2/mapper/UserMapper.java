package petro.post_hub2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import petro.post_hub2.model.dto.RoleDto;
import petro.post_hub2.model.dto.UserProfileDto;
import petro.post_hub2.model.entity.Role;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.enums.RegistrationStatus;
import petro.post_hub2.model.request.RegistrationRequest;

import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {Object.class, RegistrationStatus.class}
)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "registrationStatus", expression = "java(RegistrationStatus.ACTIVE)")
    User convertRegistrationRequestToUser(RegistrationRequest registrationRequest);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "roles", expression = "java(convertRolesToRoleDto(user.getRoles()))")
    UserProfileDto convertUserToUserProfileDto(User user, String token, String refreshToken);

    default List<RoleDto> convertRolesToRoleDto(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .toList();
    }
}

