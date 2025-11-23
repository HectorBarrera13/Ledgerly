package toast.appback.src.auth.infrastructure.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.infrastructure.api.dto.AuthMapper;
import toast.appback.src.auth.infrastructure.api.dto.request.AccountLoginRequest;
import toast.appback.src.auth.infrastructure.api.dto.request.RegisterAccountRequest;
import toast.appback.src.auth.infrastructure.api.dto.response.AuthResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RefreshTokenResponse;
import toast.appback.src.auth.infrastructure.service.RefreshCookiesService;
import toast.appback.src.auth.infrastructure.service.transactional.AuthenticateAccountService;
import toast.appback.src.auth.infrastructure.service.transactional.RegisterAccountService;
import toast.appback.src.auth.infrastructure.service.transactional.TerminateSessionService;

@RestController
@RequestMapping(AuthController.PATH)
@RequiredArgsConstructor
public class AuthController {
    static final String PATH = "/auth";
    private static final String CLIENT_TYPE_HEADER = "X-Client-Type";

    private final RegisterAccountService registerAccount;
    private final AuthenticateAccountService authenticateAccount;
    private final TerminateSessionService terminateSession;
    private final RefreshSession refreshSession;
    private final RefreshCookiesService refreshCookiesService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestHeader(name = CLIENT_TYPE_HEADER, defaultValue = "") String clientType,
            @RequestBody RegisterAccountRequest requestBody,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        boolean isMobile = "mobile".equalsIgnoreCase(clientType);
        String contextPath = request.getContextPath() + PATH;

        AuthResult result = registerAccount.execute(requestBody.toCommand());
        String refreshToken = result.tokens().refreshToken().value();

        if (!isMobile) {
            refreshCookiesService.setRefreshTokenCookie(response, refreshToken, contextPath); // 15 days
        }

        AuthResponse authResponse = AuthMapper.authToResponse(result, !isMobile);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestHeader(name = CLIENT_TYPE_HEADER, defaultValue = "") String clientType,
            @RequestBody AccountLoginRequest requestBody,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        boolean isMobile = "mobile".equalsIgnoreCase(clientType);
        String contextPath = request.getContextPath() + PATH;

        AuthResult result = authenticateAccount.execute(requestBody.toCommand());
        String refreshToken = result.tokens().refreshToken().value();

        if (!isMobile) {
            refreshCookiesService.setRefreshTokenCookie(response, refreshToken, contextPath); // 15 days
        }

        AuthResponse authResponse = AuthMapper.authToResponse(result, !isMobile);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            HttpServletRequest request
    ) {
        System.out.println("Received refresh request");
        String refreshToken = refreshCookiesService.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            refreshToken = refreshCookiesService.extractTokenFromAuthorizationHeader(authHeader);
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var accessToken = refreshSession.execute(refreshToken);

        return ResponseEntity.ok(
                new RefreshTokenResponse(
                        accessToken.value(),
                        accessToken.expiresAt()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization") String authHeader,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String contextPath = request.getContextPath() + PATH;
        String accessToken = refreshCookiesService.extractTokenFromAuthorizationHeader(authHeader);
        terminateSession.execute(accessToken);
        // Delete cookie for web clients
        refreshCookiesService.deleteRefreshCookie(response, contextPath);
        return ResponseEntity.noContent().build();
    }
}