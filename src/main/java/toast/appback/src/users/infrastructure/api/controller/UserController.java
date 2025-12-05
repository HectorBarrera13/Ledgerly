package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.infrastructure.api.dto.UserResponseMapper;
import toast.appback.src.users.infrastructure.api.dto.request.EditUserRequest;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.appback.src.users.infrastructure.service.transactional.EditUserService;
import toast.appback.src.users.infrastructure.service.transactional.SaveProfilePictureService;
import toast.model.entities.users.UserEntity;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserReadRepository userReadRepository;
    private final EditUserService editUserService;
    private final SaveProfilePictureService saveProfilePictureService;
    private final JpaUserRepository jpaUserRepository;
    private final UserResponseMapper userResponseMapper;
    @Value("${app.base-url}")
    private String baseUrl;

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
        var userResponse = userResponseMapper.toUserResponse(userView);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/upload-picture")
    public ResponseEntity<Void> uploadPicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("picture") MultipartFile picture
    ) {
        if (picture.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String contentType = picture.getContentType();

            byte[] fileBytes = picture.getBytes();
            saveProfilePictureService.execute(
                    customUserDetails.getUserId(),
                    fileBytes,
                    contentType
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/picture")
    public ResponseEntity<Picture> getProfilePicture(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String fileName = jpaUserRepository.findByUserId(customUserDetails.getUserId().getValue())
                .map(UserEntity::getProfilePictureFileName)
                .orElse(null);
        if (fileName == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Picture(baseUrl + "files/image/profile/" + fileName));
    }

    public record Picture(
            String url
    ) {
    }
}
