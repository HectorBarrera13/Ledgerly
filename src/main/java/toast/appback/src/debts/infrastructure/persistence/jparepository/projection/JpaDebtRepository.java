package toast.appback.src.debts.infrastructure.persistence.jparepository.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.debt.DebtEntity;

public interface JpaDebtRepository extends JpaRepository<DebtEntity, Long> {
}
