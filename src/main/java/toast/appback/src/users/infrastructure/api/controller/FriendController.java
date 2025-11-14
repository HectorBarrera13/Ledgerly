package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.port.FriendReadRepository;
import toast.appback.src.users.application.usecase.contract.AddFriend;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.api.dto.UserMapper;
import toast.appback.src.users.infrastructure.api.dto.response.FriendResponse;
import toast.appback.src.users.infrastructure.service.FriendRequestQRService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendRequestQRService friendRequestQRService;
    private final AddFriend addFriend;
    private final RemoveFriend removeFriend;
    private final FriendReadRepository friendReadRepository;

    @GetMapping()
    public ResponseEntity<Pageable<FriendResponse, UUID>> getFriends(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<FriendResponse> friendsContent;
        if (cursor == null) {
            friendsContent = friendReadRepository.findFriendsByUserId(userId, limit)
                    .stream().map(UserMapper::toFriendResponse).toList();
        } else {
            friendsContent = friendReadRepository.findFriendsByUserIdAfterCursor(userId, cursor, limit)
                    .stream().map(UserMapper::toFriendResponse).toList();
        }
        if (friendsContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        UUID nextCursor = friendsContent.getLast().id();
        var response = new Pageable<>(
                friendsContent,
                nextCursor
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/qr", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> generateFriendRequestQRCode(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserId userId = customUserDetails.getUserId();
        byte[] qrCodeImage = friendRequestQRService.generateQR(userId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(qrCodeImage);
    }

    @PostMapping("/{receiverId}")
    public ResponseEntity<FriendResponse> addFriend(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("receiverId") UUID receiverId
    ) {
        UserId requesterId = customUserDetails.getUserId();
        AddFriendCommand command = new AddFriendCommand(requesterId, UserId.load(receiverId));
        FriendView friendView = addFriend.execute(command);
        FriendResponse friendResponse = UserMapper.toFriendResponse(friendView);
        return ResponseEntity.ok(friendResponse);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("friendId") UUID friendId
    ) {
        UserId userId = customUserDetails.getUserId();
        RemoveFriendCommand command = new RemoveFriendCommand(userId, UserId.load(friendId));
        removeFriend.execute(command);
        return ResponseEntity.noContent().build();
    }
}
