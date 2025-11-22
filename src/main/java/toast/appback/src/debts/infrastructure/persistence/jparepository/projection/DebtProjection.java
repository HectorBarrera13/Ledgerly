package toast.appback.src.debts.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface DebtProjection {
    UUID getDebtId();
    String getPurpose();
    String getDescription();
    Long getAmount();
    String getCurrency();
    String getDebtorName();
    String getCreditorName();
    String getStatus();
}
