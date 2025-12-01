package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.infrastructure.api.dto.UserResponseMapper;
import toast.appback.src.users.infrastructure.api.dto.request.EditUserRequest;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;
import toast.appback.src.users.infrastructure.service.transactional.EditUserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserReadRepository userReadRepository;
    private final EditUserService editUserService;

    @GetMapping("/count")
    public Long getUserCount() {
        return userReadRepository.count();
    }

    @PatchMapping()
    public ResponseEntity<UserResponse> editUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EditUserRequest editUserRequest
    ) {
        var command = editUserRequest.toCommand(customUserDetails.getUserId());
        var userView = editUserService.execute(command);
        var userResponse = UserResponseMapper.toUserResponse(userView);
        return ResponseEntity.ok(userResponse);
    }
}
