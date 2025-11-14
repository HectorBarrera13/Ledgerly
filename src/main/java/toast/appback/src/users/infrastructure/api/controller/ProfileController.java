package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.application.usecase.contract.EditUser;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.api.dto.UserMapper;
import toast.appback.src.users.infrastructure.api.dto.request.EditUserRequest;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserReadRepository userReadRepository;
    private final EditUser editUser;

    @GetMapping()
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Optional<UserView> user = userReadRepository.findById(userDetails.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(user);
        UserView userView = user.get();
        UserResponse userResponse = UserMapper.toUserResponse(userView);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping()
    public ResponseEntity<UserResponse> editProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody EditUserRequest editUserRequest
            ) {
        UserId userId = userDetails.getUserId();
        EditUserCommand editUserCommand = new EditUserCommand(
                userId,
                editUserRequest.firstName(),
                editUserRequest.lastName()
        );
        UserView updatedUser = editUser.execute(editUserCommand);
        UserResponse userResponse = UserMapper.toUserResponse(updatedUser);
        return ResponseEntity.ok(userResponse);
    }
}
