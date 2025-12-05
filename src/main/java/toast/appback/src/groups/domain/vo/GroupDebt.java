package toast.appback.src.groups.domain.vo;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.event.GroupDebtAdded;
import toast.appback.src.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Object que enlaza una deuda (`DebtId`) con un grupo (`GroupId`).
 * <p>
 * Registra eventos de dominio cuando se asocia una deuda al grupo.
 */
public class GroupDebt {
    private final GroupId groupId;
    private final DebtId debtId;
    private final Instant createdAt;
    private final List<DomainEvent> groupDebtEvents = new ArrayList<>();

    private GroupDebt(GroupId groupId, DebtId debtId, Instant createdAt) {
        this.groupId = groupId;
        this.debtId = debtId;
        this.createdAt = createdAt;
    }

    /**
     * Crea una asociación entre grupo y deuda y registra {@link GroupDebtAdded}.
     */
    public static GroupDebt create(GroupId groupId, DebtId debtId) {
        Instant now = Instant.now();
        GroupDebt grupalDebt = new GroupDebt(groupId, debtId, now);

        grupalDebt.recordEvent(
                new GroupDebtAdded(
                        groupId,
                        debtId
                )
        );

        return grupalDebt;
    }

    public static GroupDebt load(GroupId groupId, DebtId debtId, Instant createdAt) {
        return new GroupDebt(groupId, debtId, createdAt);
    }

    public void recordEvent(DomainEvent event) {
        this.groupDebtEvents.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(groupDebtEvents);
        groupDebtEvents.clear();
        return events;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public DebtId getDebtId() {
        return debtId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<DomainEvent> getGroupDebtEvents() {
        return groupDebtEvents;
    }
}
