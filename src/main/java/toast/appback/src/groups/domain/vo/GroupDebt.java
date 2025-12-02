package toast.appback.src.groups.domain.vo;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.event.GroupDebtAdded;
import toast.appback.src.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GroupDebt {
    private final GroupId groupId;              // ID del grupo dueño de la deuda
    private final DebtId debtId;                // ID de la deuda asociada
    private final Instant createdAt;            // Momento en que se creó la relación
    private final List<DomainEvent> groupDebtEvents = new ArrayList<>(); // Eventos de dominio acumulados

    private GroupDebt(GroupId groupId, DebtId debtId, Instant createdAt) {
        this.groupId = groupId;
        this.debtId = debtId;
        this.createdAt = createdAt;
    }

    public static GroupDebt create(GroupId groupId, DebtId debtId) {
        Instant now = Instant.now(); // Marca de tiempo de la creación
        GroupDebt grupalDebt = new GroupDebt(groupId, debtId, now);

        grupalDebt.recordEvent(
                new GroupDebtAdded(
                        groupId,
                        debtId
                )
        ); // Registra evento de creación

        return grupalDebt;
    }

    public static GroupDebt load(GroupId groupId, DebtId debtId, Instant createdAt) {
        return new GroupDebt(groupId, debtId, createdAt); // Carga sin disparar eventos
    }

    public void recordEvent(DomainEvent event) {
        this.groupDebtEvents.add(event); // Acumula evento
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(groupDebtEvents); // Copia eventos pendientes
        groupDebtEvents.clear(); // Limpia después de extraer
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
