package toast.appback.src.users.application.communication.command;

import java.util.UUID;

public record RemoveFriendCommand(
        UUID requesterId,
        UUID friendId
) {
}
