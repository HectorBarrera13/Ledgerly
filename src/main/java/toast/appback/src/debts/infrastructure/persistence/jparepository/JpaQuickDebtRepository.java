package toast.appback.src.debts.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.debt.QuickDebtEntity;

public interface JpaQuickDebtRepository extends JpaRepository<QuickDebtEntity, Long> {
}
