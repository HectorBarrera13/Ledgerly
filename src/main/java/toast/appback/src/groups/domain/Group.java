package toast.appback.src.groups.domain;

import toast.appback.src.groups.domain.event.GroupCreated;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private GroupId id;
    private GroupInformation groupInformation;
    private Instant createdAt;
    private List<DomainEvent> groupEvents = new ArrayList<>();

    private Group(GroupId id, GroupInformation groupInformation, Instant createdAt) {
        this.id = id;
        this.groupInformation = groupInformation;
        this.createdAt = createdAt;
    }

    private Group(GroupId id, GroupInformation groupInformation, Instant createdAt, List<DomainEvent> groupEvents) {
        this(id, groupInformation, createdAt);
        this.groupEvents = groupEvents;
    }

    public static Group create(GroupInformation groupInformation, Instant createdAt) {
        GroupId groupId = GroupId.generate();
        Group group = new Group(groupId, groupInformation, createdAt);
        group.recordEvent(new GroupCreated(groupId, groupInformation));
        return group;
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

    public List<DomainEvent> getGroupEvents() {
        return groupEvents;
    }
}
