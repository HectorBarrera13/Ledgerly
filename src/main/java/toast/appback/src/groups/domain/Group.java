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
    private GroupId id;                                   // Identificador del grupo
    private GroupInformation groupInformation;            // Nombre y descripción validados del grupo
    private UserId creatorId;                             // Usuario que creó el grupo
    private Instant createdAt;                            // Fecha de creación
    private List<DomainEvent> groupEvents = new ArrayList<>(); // Eventos de dominio pendientes

    private Group(GroupId id, GroupInformation groupInformation, UserId creatorId, Instant createdAt) {
        this.id = id;
        this.creatorId = creatorId;
        this.groupInformation = groupInformation;
        this.createdAt = createdAt;
    }

    private Group(GroupId id, GroupInformation groupInformation, UserId creatorId, Instant createdAt, List<DomainEvent> groupEvents) {
        this(id, groupInformation, creatorId, createdAt);
        this.groupEvents = groupEvents; // Carga con eventos ya persistidos
    }

    public static Group create(GroupInformation groupInformation,UserId creatorId) {
        GroupId groupId = GroupId.generate();
        Instant createdAt = Instant.now();
        Group group = new Group(groupId, groupInformation, creatorId, createdAt);

        group.recordEvent(new GroupCreated(groupId, groupInformation)); // Evento de creación

        return group;
    }

    public static Group Load(
            GroupId id,
            GroupInformation groupInformation,
            UserId creatorId,
            Instant createdAt
    ) {
        return new Group(id, groupInformation, creatorId, createdAt); // Carga sin eventos nuevos
    }

    public Result<Void, DomainError> editGroupInformation(GroupInformation groupInformation) {
        this.groupInformation = groupInformation; // Actualiza información del grupo
        return Result.ok();
    }

    public void recordEvent(DomainEvent domainEvent) {
        this.groupEvents.add(domainEvent); // Acumula evento
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(this.groupEvents); // Copia eventos
        this.groupEvents.clear(); // Limpia cola de eventos
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

