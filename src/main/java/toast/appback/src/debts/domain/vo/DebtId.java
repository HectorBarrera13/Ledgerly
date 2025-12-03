package toast.appback.src.debts.domain.vo;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una deuda (Debt).
 */
public class DebtId {
    private final UUID id;

    /**
     * Constructor privado para forzar el uso de factory methods.
     */
    private DebtId(UUID id) {
        this.id = id;
    }

    public UUID getValue() {
        return id;
    }

    /**
     * Factory method para generar un nuevo DebtId con UUID aleatorio.
     * Usado al crear nuevas entidades en dominio.
     */
    public static DebtId generate() {
        return new DebtId(UUID.randomUUID());
    }

    /**
     * Factory method para reconstruir un DebtId desde un UUID existente.
     * Usado al cargar desde persistencia o al mapear DTOs.
     */
    public static DebtId load(UUID uuid) {
        return new DebtId(uuid);
    }

    /**
     * Dos DebtId son iguales si el UUID interno es igual.
     * Implementación estándar de Value Object Identity.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DebtId debtId = (DebtId) o;
        return Objects.equals(id, debtId.id);
    }

    /**
     * Mantiene la consistencia con equals.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

