package petro.post_hub2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import petro.post_hub2.model.dto.UserProfileDto;
import petro.post_hub2.model.request.LoginRequest;
import petro.post_hub2.model.request.RegistrationRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.service.AuthService;
import petro.post_hub2.utils.ApiUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Auth", description = "Auth methods")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Auth user and returns tokens"
    )
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "Successful auth",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                                "message": "token created successfully",
                                "body": {
                                    "id": 5,
                                    "username": "petro2",
                                    "email": "petro2@gmail.com",
                                    "updated": "2026-07-14T17:23:19.049856",
                                    "registrationStatus": "ACTIVE",
                                    "lastLogin": "2026-07-14T17:23:19.049856",
                                    "token": "eyJhbGci...",
                                    "refreshToken": "187174b8...",
                                    "roles": [
                                        {
                                            "id": 3,
                                            "name": "USER"
                                        }
                                    ]
                                },
                                "success": true
                            }
                            """))
    )
    )
    public ResponseEntity<CustomResponse<UserProfileDto>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                                HttpServletResponse response) {
        CustomResponse<UserProfileDto> loginResponse = authService.login(loginRequest);
        Cookie cookie = ApiUtils.createAuthCookie(loginResponse.getBody().getToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/registration")
    @Operation(
            summary = "User registration",
            description = "Register user and returns tokens"
    )
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "Successful registration",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "message": "token created successfully",
                                "body": {
                                    "id": 7,
                                    "username": "petro3",
                                    "email": "petro3@gmail.com",
                                    "updated": "2026-07-18T14:51:40.0451405",
                                    "registrationStatus": "ACTIVE",
                                    "lastLogin": "2026-07-18T14:51:40.0451405",
                                    "token": "eyJhbGci...",
                                    "refreshToken": "1d2d8d25...",
                                    "roles": [
                                        {
                                            "id": 3,
                                            "name": "USER"
                                        }
                                    ]
                                },
                                "success": true
                            }
                            """)
            )

    )
    )
    public ResponseEntity<CustomResponse<UserProfileDto>> registration(@Valid @RequestBody RegistrationRequest registrationRequest,
                                                                       HttpServletResponse response) {
        CustomResponse<UserProfileDto> registrationResponse = authService.registration(registrationRequest);
        Cookie cookie = ApiUtils.createAuthCookie(registrationResponse.getBody().getToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(registrationResponse);
    }

    @GetMapping("/refreshToken")
    @Operation(summary = "User refreshToken",
            description = "Refresh token and returns tokens"
    )
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "Successful refresh token",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "message": "token created successfully",
                                "body": {
                                    "id": 5,
                                    "username": "petro2",
                                    "email": "petro2@gmail.com",
                                    "updated": "2026-07-14T17:23:19.049856",
                                    "registrationStatus": "ACTIVE",
                                    "lastLogin": "2026-07-14T17:23:19.049856",
                                    "token": "eyJhbGci...",
                                    "refreshToken": "037862e4...",
                                    "roles": [
                                        {
                                            "id": 3,
                                            "name": "USER"
                                        }
                                    ]
                                },
                                "success": true
                            }
                            """)
            )
    ))
    public ResponseEntity<CustomResponse<UserProfileDto>> refreshToken(@RequestParam String refreshToken,
                                                                       HttpServletResponse response) {
        CustomResponse<UserProfileDto> refreshTokenResponse = authService.refreshToken(refreshToken);
        Cookie cookie = ApiUtils.createAuthCookie(refreshTokenResponse.getBody().getToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(refreshTokenResponse);
    }
}
