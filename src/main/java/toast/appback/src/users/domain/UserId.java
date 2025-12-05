package toast.appback.src.users.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que envuelve un {@link UUID} para identificar de forma única a un usuario.
 *
 * <p>Notas:
 * - Proporciona fábricas para generar un nuevo identificador (`generate`) o cargar uno existente (`load`).
 */
public class UserId {

    private final UUID value;

    private UserId(UUID value) {
        this.value = value;
    }

    /**
     * Genera un nuevo {@link UserId} con un UUID aleatorio.
     *
     * @return Nuevo `UserId` único.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Crea un {@link UserId} a partir de un {@link UUID} existente.
     *
     * @param uuid UUID existente.
     * @return `UserId` que envuelve el UUID dado.
     */
    public static UserId load(UUID uuid) {
        return new UserId(uuid);
    }

    /**
     * @return El valor {@link UUID} subyacente.
     */
    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserId userId)) return false;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
