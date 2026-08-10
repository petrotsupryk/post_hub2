package petro.post_hub2.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ApiErrorMessage {
    REFRESH_TOKEN_NOT_FOUND_BY_TOKEN("Refresh token not found by token"),
    USER_NOT_FOUND_BY_EMAIL("user not found by email: %s"),
    INVALID_EMAIL_OR_PASSWORD("Invalid email or password"),
    USER_WITH_THIS_EMAIL_ALREADY_EXISTS("user with %s email already exists"),
    USER_WITH_THIS_USERNAME_ALREADY_EXISTS("user with %s username already exists"),
    PASSWORD_DOES_NOT_MATCH("password does not match"),
    INVALID_PASSWORD("invalid password"),
    ROLE_NOT_FOUND_BY_NAME("role not found by name %s"),
    TOKEN_EXPIRED("token expired"),
    INVALID_TOKEN_SIGNATURE("invalid token signature"),
    DONT_HAVE_ACCESS("this user don't have access to do this action"),
    POST_NOT_FOUND_BY_ID("not found post by id: %s"),
    POST_WITH_THIS_TITLE_ALREADY_EXISTS("post with title %s already exists"),
    USER_NOT_FOUND_BY_ID("not found user by id %s");
    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }

}
