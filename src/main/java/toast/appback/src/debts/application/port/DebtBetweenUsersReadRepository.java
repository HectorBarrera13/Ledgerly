package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de lectura para obtener vistas de deuda entre usuarios.
 */
public interface DebtBetweenUsersReadRepository {
    /**
     * Busca una vista por su identificador.
     *
     * @param debtId Identificador de la deuda.
     * @return Optional con la vista si existe.
     */
    Optional<DebtBetweenUsersView> findById(DebtId debtId);

    /**
     * Lista de deudas relacionadas con un usuario, filtrando por rol y estado.
     *
     * @param userId Identificador del usuario.
     * @param role   Rol del usuario (DEBTOR/CREDITOR).
     * @param status Filtro de estado (opcional, puede ser null o "ALL").
     * @param limit  Límite de resultados.
     * @return Lista de vistas resumidas.
     */
    List<DebtBetweenUsersView> getDebtsBetweenUsers(UserId userId, String role, String status, int limit);

    /**
     * Paginación basada en cursor (debtId) para listados de deudas entre usuarios.
     */
    List<DebtBetweenUsersView> getDebtsBetweenUsersAfterCursor(UserId userId, String role, String status, UUID cursor, int limit);
}
