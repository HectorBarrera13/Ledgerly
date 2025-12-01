package toast.appback.src.debts.infrastructure.persistence.jparepository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface DebtProjection {
    UUID getDebtId();
    String getPurpose();
    String getDescription();
    BigDecimal getAmount();
    String getCurrency();
    String getStatus();
}
