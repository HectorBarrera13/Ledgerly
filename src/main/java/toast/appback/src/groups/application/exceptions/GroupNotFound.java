package toast.appback.src.groups.application.exceptions;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class GroupNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "group with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "group not found.";
    private final UUID groupId;

    public GroupNotFound(GroupId groupId) {
        super(String.format(MESSAGE_TEMPLATE, groupId), FRIENDLY_MESSAGE);
        this.groupId = groupId.getValue();
    }

    public GroupId getGroupId() {
        return GroupId.load(groupId);
    }
}
