package toast.appback.src.groups.domain.vo;

import java.util.UUID;

/**
 * Value Object que representa el identificador único de un grupo.
 */
public class GroupId {
    private final UUID id;

    private GroupId(UUID id) {
        this.id = id;
    }

    /**
     * Genera un nuevo GroupId aleatorio.
     */
    public static GroupId generate() {
        return new GroupId(UUID.randomUUID());
    }

    /**
     * Reconstruye un GroupId a partir de un UUID existente.
     */
    public static GroupId load(UUID id) {
        return new GroupId(id);
    }

    /**
     * @return UUID subyacente.
     */
    public UUID getValue() {
        return id;
    }
}
