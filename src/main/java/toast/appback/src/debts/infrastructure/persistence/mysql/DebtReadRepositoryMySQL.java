package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.JpaDebtRepository;

@Repository
@RequiredArgsConstructor
public class DebtReadRepositoryMySQL {
    private final JpaDebtRepository jpaDebtRepository;


}
