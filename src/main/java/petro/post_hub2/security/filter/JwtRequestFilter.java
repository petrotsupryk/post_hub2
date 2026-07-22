package petro.post_hub2.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.JwtTokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTRATION_PATH = "/auth/registration";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
        String requestUri = request.getRequestURI();
        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER_PREFIX)) {
            String jwtToken = authHeader.get().substring(BEARER_PREFIX.length());
            try {
                if (!jwtTokenProvider.validateToken(jwtToken)) {
                    throw new ExpiredJwtException(null, null, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
                }
                Optional<String> emailOptional = Optional.ofNullable(jwtTokenProvider.getEmailFromToken(jwtToken));
                emailOptional.ifPresent(email -> {
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = userRepository.findByEmailAndDeletedFalse(email)
                                .orElseThrow(() ->
                                        new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(emailOptional.get())));
                        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(jwtToken).stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                user, null, authorities
                        );
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                });

            } catch (ExpiredJwtException e) {
                handleExpiredJwtException(requestUri, jwtToken, response);
                return;
            } catch (MalformedJwtException e) {
                handleSignatureException(response);
                return;
            } catch (Exception e) {
                handleUnExpectedException(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleExpiredJwtException(String url, String jwt, HttpServletResponse response) throws IOException {
        if (url.equals(LOGIN_PATH) || url.equals(REGISTRATION_PATH)) {
            String refreshToken = jwtTokenProvider.refreshToken(jwt);
            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshToken);
        } else {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
    }

    private void handleSignatureException(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.INVALID_TOKEN_SIGNATURE.getMessage());
    }

    private void handleUnExpectedException(HttpServletResponse response, Exception e) throws IOException {
        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
