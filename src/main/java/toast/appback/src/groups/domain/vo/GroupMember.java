package toast.appback.src.groups.domain.vo;

import toast.appback.src.groups.domain.event.GroupMemberAdded;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GroupMember {
    private final GroupId groupId;                  // Grupo al que pertenece el usuario
    private final UserId userId;                    // Usuario que se une al grupo
    private final Instant createdAt;                // Momento en que se creó la membresía
    private final List<DomainEvent> memberEvents = new ArrayList<>(); // Eventos de dominio acumulados

    public GroupMember(GroupId groupId, UserId userId, Instant createdAt) {
        this.groupId = groupId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static GroupMember create(GroupId groupId, UserId userId) {
        Instant now = Instant.now(); // Marca temporal de creación
        GroupMember groupMember = new GroupMember(groupId, userId, now);

        groupMember.recordEvent(
                new GroupMemberAdded(
                        groupId,
                        userId
                )
        ); // Registra evento de agregado

        return groupMember;
    }

    public static GroupMember load(GroupId groupId, UserId userId, Instant createdAt) {
        return new GroupMember(groupId, userId, createdAt); // Carga sin disparar eventos
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(memberEvents); // Copia eventos pendientes
        memberEvents.clear(); // Limpia la cola de eventos
        return events;
    }

    public void recordEvent(DomainEvent domainEvent) {
        this.memberEvents.add(domainEvent); // Acumula evento
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

