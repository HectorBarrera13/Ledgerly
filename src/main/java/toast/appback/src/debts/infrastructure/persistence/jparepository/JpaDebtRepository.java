package toast.appback.src.debts.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtProjection;
import toast.model.entities.debt.DebtEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaDebtRepository extends JpaRepository<DebtEntity, Long> {
        Optional<DebtEntity> findByDebtId(UUID debtId);

        @Query("""
            SELECT d.debtId AS debtId,d.purpose AS purpose, d.description AS description,
                   d.amount AS amount, d.currency AS currency,
                   d.debtorName AS debtorName, d.creditorName AS creditorName,
                   d.status AS status
            FROM DebtEntity d
            WHERE d.debtId = :debtId
        """)
        Optional<DebtProjection> findDebtProjectionByDebtId(@Param("debtId")UUID debtId);

}
