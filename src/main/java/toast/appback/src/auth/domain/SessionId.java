package toast.appback.src.auth.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que envuelve un {@link UUID} para identificar de forma única una sesión.
 */
public class SessionId {

    private final UUID value;

    private SessionId(UUID value) {
        this.value = value;
    }

    /**
     * Carga un {@link SessionId} existente a partir de un UUID.
     *
     * @param uuid UUID existente.
     * @return SessionId que envuelve el UUID.
     */
    public static SessionId load(UUID uuid) {
        return new SessionId(uuid);
    }

    /**
     * Genera un nuevo {@link SessionId} único.
     *
     * @return Nuevo SessionId.
     */
    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    /**
     * @return El valor {@link UUID} subyacente.
     */
    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SessionId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SessionId sessionId)) return false;
        return Objects.equals(value, sessionId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
