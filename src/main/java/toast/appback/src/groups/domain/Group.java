package toast.appback.src.groups.domain;

import toast.appback.src.groups.domain.event.GroupCreated;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private GroupId id;
    private GroupInformation groupInformation;
    private UserId creatorId;
    private Instant createdAt;
    private List<DomainEvent> groupEvents = new ArrayList<>();

    private Group(GroupId id, GroupInformation groupInformation, UserId creatorId, Instant createdAt) {
        this.id = id;
        this.creatorId = creatorId;
        this.groupInformation = groupInformation;
        this.createdAt = createdAt;
    }

    private Group(GroupId id, GroupInformation groupInformation, UserId creatorId, Instant createdAt, List<DomainEvent> groupEvents) {
        this(id, groupInformation, creatorId, createdAt);
        this.groupEvents = groupEvents;
    }

    public static Group create(GroupInformation groupInformation,UserId creatorId) {
        GroupId groupId = GroupId.generate();
        Instant createdAt = Instant.now();
        Group group = new Group(groupId, groupInformation, creatorId, createdAt);
        group.recordEvent(new GroupCreated(groupId, groupInformation));
        return group;
    }

    public static Group Load(
            GroupId id,
            GroupInformation groupInformation,
            UserId creatorId,
            Instant createdAt
    ) {
        return new Group(id, groupInformation, creatorId, createdAt);
    }

    public Result<Void, DomainError> editGroupInformation(GroupInformation groupInformation) {
        this.groupInformation = groupInformation;
        return Result.ok();
    }

    public void recordEvent(DomainEvent domainEvent) {
        this.groupEvents.add(domainEvent);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(this.groupEvents);
        this.groupEvents.clear();
        return events;
    }

    public GroupId getId() {
        return id;
    }

    public GroupInformation getGroupInformation() {
        return groupInformation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserId getCreatorId() {
        return creatorId;
    }

    public List<DomainEvent> getGroupEvents() {
        return groupEvents;
    }
}
