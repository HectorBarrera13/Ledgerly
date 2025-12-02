package toast.appback.src.groups.domain.vo;

import org.junit.jupiter.api.Test;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.event.GroupDebtAdded;
import toast.appback.src.shared.domain.DomainEvent;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class GroupDebtTest {

    @Test
    void testCreateGeneratesEvent() {
        GroupId groupId = GroupId.generate();
        DebtId debtId = DebtId.generate();

        GroupDebt groupDebt = GroupDebt.create(groupId, debtId);

        assertNotNull(groupDebt.getCreatedAt());
        assertEquals(groupId, groupDebt.getGroupId());
        assertEquals(debtId, groupDebt.getDebtId());
        assertEquals(1, groupDebt.getGroupDebtEvents().size());
        assertTrue(groupDebt.getGroupDebtEvents().get(0) instanceof GroupDebtAdded);
    }

    @Test
    void testLoadDoesNotGenerateEvent() {
        GroupId groupId = GroupId.generate();
        DebtId debtId = DebtId.generate();
        Instant now = Instant.now();

        GroupDebt groupDebt = GroupDebt.load(groupId, debtId, now);

        assertEquals(0, groupDebt.getGroupDebtEvents().size());
        assertEquals(now, groupDebt.getCreatedAt());
    }

    @Test
    void testRecordEventAddsEvent() {
        GroupId groupId = GroupId.generate();
        DebtId debtId = DebtId.generate();

        GroupDebt groupDebt = GroupDebt.load(groupId, debtId, Instant.now());

        DomainEvent event = new GroupDebtAdded(groupId, debtId);
        groupDebt.recordEvent(event);

        assertEquals(1, groupDebt.getGroupDebtEvents().size());
        assertEquals(event, groupDebt.getGroupDebtEvents().get(0));
    }

    @Test
    void testPullEventsClearsAndReturnsEvents() {
        GroupId groupId = GroupId.generate();
        DebtId debtId = DebtId.generate();

        GroupDebt groupDebt = GroupDebt.create(groupId, debtId);

        List<DomainEvent> events = groupDebt.pullEvents();

        assertEquals(1, events.size());
        assertEquals(0, groupDebt.getGroupDebtEvents().size());
    }

    @Test
    void testGetters() {
        GroupId groupId = GroupId.generate();
        DebtId debtId = DebtId.generate();
        Instant now = Instant.now();

        GroupDebt gd = GroupDebt.load(groupId, debtId, now);

        assertEquals(groupId, gd.getGroupId());
        assertEquals(debtId, gd.getDebtId());
        assertEquals(now, gd.getCreatedAt());
    }
}