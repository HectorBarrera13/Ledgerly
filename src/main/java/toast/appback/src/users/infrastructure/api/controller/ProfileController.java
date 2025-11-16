package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.port.AccountReadRepository;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.infrastructure.api.dto.UserMapper;
import toast.appback.src.users.infrastructure.api.dto.response.ProfileResponse;

import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserReadRepository userReadRepository;
    private final AccountReadRepository accountReadRepository;

    @GetMapping()
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Optional<AccountView> accountView = accountReadRepository.findById(userDetails.getAccountId());
        if (accountView.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<UserView> userView = userReadRepository.findById(userDetails.getUserId());
        if (userView.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProfileResponse profileResponse = UserMapper.toProfileResponse(accountView.get(), userView.get());
        return ResponseEntity.ok(profileResponse);
    }
}
