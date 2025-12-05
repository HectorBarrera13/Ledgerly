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

/**
 * Agregado de dominio que representa un grupo de usuarios.
 *
 * <p>Contiene información básica del grupo, referencia al creador y eventos de dominio.
 * Las operaciones de negocio se realizan a través de factory methods y métodos de instancia.
 */
public class Group {
    private final GroupId id;
    private final UserId creatorId;
    private final Instant createdAt;
    private GroupInformation groupInformation;
    private List<DomainEvent> groupEvents = new ArrayList<>();

    /*
     * Private constructor to enforce the use of factory methods
     */
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

    /**
     * Crea un nuevo grupo validando la información y generando el evento {@link GroupCreated}.
     *
     * @param groupInformation Información del grupo (name, description).
     * @param creatorId        Identificador del usuario que crea el grupo.
     * @return Instancia de {@link Group} creada.
     */
    public static Group create(GroupInformation groupInformation, UserId creatorId) {
        GroupId groupId = GroupId.generate();
        Instant createdAt = Instant.now();
        Group group = new Group(groupId, groupInformation, creatorId, createdAt);

        group.recordEvent(new GroupCreated(groupId, groupInformation));

        return group;
    }

    /**
     * Reconstruye una entidad {@link Group} desde datos persistidos.
     */
    public static Group Load(
            GroupId id,
            GroupInformation groupInformation,
            UserId creatorId,
            Instant createdAt
    ) {
        return new Group(id, groupInformation, creatorId, createdAt);
    }

    /**
     * Edita la información del grupo (nombre/descr) y devuelve resultado con errores de dominio si los hay.
     */
    public Result<Void, DomainError> editGroupInformation(GroupInformation groupInformation) {
        this.groupInformation = groupInformation;
        return Result.ok();
    }

    /**
     * Registra un evento de dominio en el agregado.
     */
    public void recordEvent(DomainEvent domainEvent) {
        this.groupEvents.add(domainEvent);
    }

    /**
     * Recupera y limpia la cola de eventos de dominio del agregado.
     */
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
