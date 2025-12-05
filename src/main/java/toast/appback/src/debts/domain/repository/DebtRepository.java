package toast.appback.src.debts.domain.repository;

import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.vo.DebtId;

import java.util.Optional;

/**
 * Repositorio de dominio para persistir y recuperar distintos tipos de deuda.
 * <p>
 * Implementaciones deben encargarse de la persistencia transaccional y rehidratación de entidades.
 */
public interface DebtRepository {
    /**
     * Persiste una deuda entre usuarios.
     *
     * @param debtBetweenUsers Entidad a guardar.
     */
    void save(DebtBetweenUsers debtBetweenUsers);

    /**
     * Persiste una deuda rápida.
     *
     * @param quickDebt Entidad a guardar.
     */
    void save(QuickDebt quickDebt);

    /**
     * Busca una deuda entre usuarios por su ID.
     *
     * @param debtId Identificador de la deuda.
     * @return Optional con la entidad si existe.
     */
    Optional<DebtBetweenUsers> findDebtBetweenUsersById(DebtId debtId);

    /**
     * Busca una deuda rápida por su ID.
     *
     * @param debtId Identificador de la deuda.
     * @return Optional con la entidad si existe.
     */
    Optional<QuickDebt> findQuickDebtById(DebtId debtId);
}
