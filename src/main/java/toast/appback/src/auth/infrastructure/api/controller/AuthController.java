package toast.appback.src.auth.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.application.usecase.contract.AccountLogin;
import toast.appback.src.auth.application.usecase.contract.AccountLogout;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;
import toast.appback.src.auth.infrastructure.api.dto.*;
import toast.appback.src.auth.infrastructure.api.dto.request.AccountLoginRequest;
import toast.appback.src.auth.infrastructure.api.dto.request.SessionRequest;
import toast.appback.src.auth.infrastructure.api.dto.request.RegisterAccountRequest;
import toast.appback.src.auth.infrastructure.api.dto.response.AccountLoginResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RefreshTokenResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RegisterAccountResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterAccount registerAccount;
    private final AccountLogin accountLogin;
    private final AccountLogout accountLogout;
    private final RefreshSession refreshSession;

    @PostMapping("/register")
    public ResponseEntity<RegisterAccountResponse> register(@RequestBody RegisterAccountRequest registerAccountRequest) {
        var result = registerAccount.execute(registerAccountRequest.toCommand());
        var response = AuthMapper.registerToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AccountLoginResponse> login(@RequestBody AccountLoginRequest accountLoginRequest) {
        var result = accountLogin.execute(accountLoginRequest.toCommand());
        var response = AuthMapper.loginToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody SessionRequest sessionRequest
            ) {
        var result = refreshSession.execute(sessionRequest.refreshToken());
        var response = AuthMapper.refreshToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody SessionRequest sessionRequest) {
        accountLogout.execute(sessionRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }

    // TODO: Implement password reset functionality
    @PatchMapping("/password-reset")
    public ResponseEntity<Void> passwordReset() {
        return null;
    }
}
