package toast.appback.src.groups.domain;

import org.junit.jupiter.api.Test;
import toast.appback.src.groups.domain.event.GroupCreated;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class GroupTest {

    @Test
    void testCreateGeneratesGroupCorrectly() {
        GroupInformation info = GroupInformation.load("Grupo X", "Descripción");
        UserId creatorId = UserId.generate();

        Group group = Group.create(info, creatorId);

        assertNotNull(group.getId());
        assertEquals(info, group.getGroupInformation());
        assertEquals(creatorId, group.getCreatorId());
        assertNotNull(group.getCreatedAt());
        assertEquals(1, group.getGroupEvents().size());
        assertTrue(group.getGroupEvents().get(0) instanceof GroupCreated);
    }

    @Test
    void testLoadDoesNotGenerateEvents() {
        GroupId id = GroupId.generate();
        GroupInformation info = GroupInformation.load("Grupo Y", "Desc");
        UserId creatorId = UserId.generate();
        Instant now = Instant.now();

        Group group = Group.Load(id, info, creatorId, now);

        assertEquals(id, group.getId());
        assertEquals(info, group.getGroupInformation());
        assertEquals(creatorId, group.getCreatorId());
        assertEquals(now, group.getCreatedAt());
        assertEquals(0, group.getGroupEvents().size());
    }

    @Test
    void testEditGroupInformationUpdatesInfo() {
        GroupInformation info1 = GroupInformation.load("Original", "Desc1");
        GroupInformation info2 = GroupInformation.load("Nuevo", "Desc2");
        UserId creatorId = UserId.generate();

        Group group = Group.create(info1, creatorId);

        var result = group.editGroupInformation(info2);

        assertTrue(result.isOk());
        assertEquals(info2, group.getGroupInformation());
    }

    @Test
    void testRecordEventAddsEvent() {
        GroupInformation info = GroupInformation.load("Grupo", "Descripción");
        UserId creatorId = UserId.generate();
        Group group = Group.create(info, creatorId);

        DomainEvent event = new DummyEvent();
        group.recordEvent(event);

        assertEquals(2, group.getGroupEvents().size()); // 1 de creación + 1 agregado
        assertEquals(event, group.getGroupEvents().get(1));
    }

    @Test
    void testPullEventsReturnsAndClears() {
        GroupInformation info = GroupInformation.load("Grupo", "Descripción");
        UserId creatorId = UserId.generate();
        Group group = Group.create(info, creatorId);

        List<DomainEvent> events = group.pullEvents();

        assertEquals(1, events.size());
        assertEquals(0, group.getGroupEvents().size());
    }

    // Evento de prueba simple
    static class DummyEvent implements DomainEvent { }
}
