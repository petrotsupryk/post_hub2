package petro.post_hub2.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

public class ApiUtils {
    public static String getMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[2].getMethodName();
        } catch (Exception e) {
            // return
            return "";
        }
    }

    public static Cookie createAuthCookie(String value) {
        Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        return cookie;
    }

    public static String generatedUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
