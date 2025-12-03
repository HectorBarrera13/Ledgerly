package toast.appback.src.groups.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record CreateGroupCommand(
        String name,
        String description,
        UserId creatorId
) {
}
