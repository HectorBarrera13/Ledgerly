package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de lectura para obtener vistas de `QuickDebt`.
 * <p>
 * Implementaciones deben proporcionar consultas optimizadas para listar y paginar deudas rápidas.
 */
public interface QuickDebtReadRepository {
    /**
     * Busca una vista de deuda rápida por su identificador.
     *
     * @param id Identificador de la deuda.
     * @return Optional con la vista si existe.
     */
    Optional<QuickDebtView> findById(DebtId id);

    /**
     * Obtiene una lista de deudas rápidas relacionadas con un usuario y su rol.
     *
     * @param userId Identificador del usuario observador.
     * @param role   Rol del usuario en la deuda (DEBTOR/CREDITOR).
     * @param limit  Límite de resultados.
     * @return Lista de vistas simplificadas de QuickDebt.
     */
    List<QuickDebtView> getQuickDebts(UserId userId, String role, int limit);

    /**
     * Paginación: obtiene QuickDebt después de un cursor (cursor-based pagination).
     *
     * @param userId Identificador del usuario.
     * @param role   Rol del usuario.
     * @param cursor Cursor (debtId) desde donde continuar.
     * @param limit  Límite de resultados.
     * @return Lista de vistas de QuickDebt posteriores al cursor.
     */
    List<QuickDebtView> getQuickDebtsAfterCursor(UserId userId, String role, UUID cursor, int limit);
}
