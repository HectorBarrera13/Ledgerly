package toast.appback.src.groups.domain.vo;

import org.junit.jupiter.api.Test;
import toast.appback.src.groups.domain.event.GroupMemberAdded;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class GroupMemberTest {

    @Test
    void testCreateGeneratesEvent() {
        GroupId groupId = GroupId.generate();
        UserId userId = UserId.generate();

        GroupMember member = GroupMember.create(groupId, userId);

        assertNotNull(member.getCreatedAt());
        assertEquals(groupId, member.getGroupId());
        assertEquals(userId, member.getUserId());
        assertEquals(1, member.getMemberEvents().size());
        assertTrue(member.getMemberEvents().get(0) instanceof GroupMemberAdded);
    }

    @Test
    void testLoadDoesNotGenerateEvent() {
        GroupId groupId = GroupId.generate();
        UserId userId = UserId.generate();
        Instant now = Instant.now();

        GroupMember member = GroupMember.load(groupId, userId, now);

        assertEquals(0, member.getMemberEvents().size());
        assertEquals(now, member.getCreatedAt());
    }

    @Test
    void testRecordEventAddsEvent() {
        GroupId groupId = GroupId.generate();
        UserId userId = UserId.generate();

        GroupMember member = GroupMember.load(groupId, userId, Instant.now());

        DomainEvent event = new GroupMemberAdded(groupId, userId);
        member.recordEvent(event);

        assertEquals(1, member.getMemberEvents().size());
        assertEquals(event, member.getMemberEvents().get(0));
    }

    @Test
    void testPullEventsClearsAndReturnsEvents() {
        GroupId groupId = GroupId.generate();
        UserId userId = UserId.generate();

        GroupMember member = GroupMember.create(groupId, userId);

        List<DomainEvent> events = member.pullEvents();

        assertEquals(1, events.size());
        assertEquals(0, member.getMemberEvents().size());
    }

    @Test
    void testGettersReturnCorrectValues() {
        GroupId groupId = GroupId.generate();
        UserId userId = UserId.generate();
        Instant now = Instant.now();

        GroupMember member = GroupMember.load(groupId, userId, now);

        assertEquals(groupId, member.getGroupId());
        assertEquals(userId, member.getUserId());
        assertEquals(now, member.getCreatedAt());
    }
}
