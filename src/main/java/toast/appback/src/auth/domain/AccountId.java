package toast.appback.src.auth.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que envuelve un {@link UUID} para identificar de forma única una cuenta.
 */
public class AccountId {

    private final UUID value;

    private AccountId(UUID value) {
        this.value = value;
    }

    /**
     * Carga un {@link AccountId} existente a partir de un UUID.
     *
     * @param uuid UUID existente.
     * @return AccountId que envuelve el UUID.
     */
    public static AccountId load(UUID uuid) {
        return new AccountId(uuid);
    }

    /**
     * Genera un nuevo {@link AccountId} único.
     *
     * @return Nuevo AccountId.
     */
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    /**
     * @return El valor {@link UUID} subyacente.
     */
    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AccountId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountId accountId)) return false;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
