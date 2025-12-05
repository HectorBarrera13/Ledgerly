package toast.appback.src.groups.domain.vo;

import toast.appback.src.groups.domain.event.GroupMemberAdded;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class GroupMember {
    private final GroupId groupId;
    private final UserId userId;
    private final Instant createdAt;
    private final List<DomainEvent> memberEvents = new ArrayList<>();

    public GroupMember(GroupId groupId, UserId userId, Instant createdAt) {
        this.groupId = groupId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    /**
     * Crea un nuevo miembro de grupo y registra el evento {@link GroupMemberAdded}.
     */
    public static GroupMember create(GroupId groupId, UserId userId) {
        Instant now = Instant.now();
        GroupMember groupMember = new GroupMember(groupId, userId, now);

        groupMember.recordEvent(
                new GroupMemberAdded(
                        groupId,
                        userId
                )
        );

        return groupMember;
    }

    public static GroupMember load(GroupId groupId, UserId userId, Instant createdAt) {
        return new GroupMember(groupId, userId, createdAt);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(memberEvents);
        memberEvents.clear();
        return events;
    }

    public void recordEvent(DomainEvent domainEvent) {
        this.memberEvents.add(domainEvent);
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public UserId getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<DomainEvent> getMemberEvents() {
        return memberEvents;
    }
}
