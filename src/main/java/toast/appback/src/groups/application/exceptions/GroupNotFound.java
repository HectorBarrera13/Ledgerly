package toast.appback.src.groups.application.exceptions;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.ApplicationException;

public class GroupNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "group with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "group not found.";
    private final GroupId groupId;

    public GroupNotFound(GroupId groupId) {
        super(String.format(MESSAGE_TEMPLATE, groupId), FRIENDLY_MESSAGE);
        this.groupId = groupId;
    }

    public GroupId getGroupId() {
        return groupId;
    }
}
