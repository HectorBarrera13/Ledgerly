package toast.appback.src.auth.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.api.dto.*;
import toast.appback.src.auth.infrastructure.api.dto.request.AccountLoginRequest;
import toast.appback.src.auth.infrastructure.api.dto.request.SessionRequest;
import toast.appback.src.auth.infrastructure.api.dto.request.RegisterAccountRequest;
import toast.appback.src.auth.infrastructure.api.dto.response.AccountLoginResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RegisterAccountResponse;
import toast.appback.src.auth.infrastructure.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterAccountResponse> register(@RequestBody RegisterAccountRequest registerAccountRequest) {
        var result = authService.registerAccount(registerAccountRequest.toCommand());
        var response = AuthMapper.registerToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AccountLoginResponse> login(@RequestBody AccountLoginRequest accountLoginRequest) {
        var result = authService.loginAccount(accountLoginRequest.toCommand());
        var response = AuthMapper.loginToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccountLoginResponse> refresh(
            @RequestBody SessionRequest sessionRequest
            ) {
        var result = authService.refreshSession(sessionRequest.refreshToken());
        var response = AuthMapper.loginToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody SessionRequest sessionRequest) {
        authService.logoutAccount(sessionRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }

    // TODO: Implement password reset functionality
    @PatchMapping("/password-reset")
    public ResponseEntity<Void> passwordReset() {
        return null;
    }
}
