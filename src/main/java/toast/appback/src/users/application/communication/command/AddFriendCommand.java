package toast.appback.src.users.application.communication.command;

import java.util.UUID;

public record AddFriendCommand(
        UUID requesterId,
        UUID receiverId
) {
}
