package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.domain.vo.DebtId;

import java.util.Optional;

/**
 * Puerto de lectura genérico para obtener vistas de deuda (cualquier implementación concreta).
 */
public interface DebtReadRepository {
    /**
     * Busca una vista de deuda por su identificador.
     *
     * @param userId Identificador de la deuda.
     * @return Optional con la vista si existe.
     */
    Optional<DebtView> findById(DebtId userId);
}
