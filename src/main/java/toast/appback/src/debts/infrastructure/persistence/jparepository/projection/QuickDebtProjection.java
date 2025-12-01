package toast.appback.src.debts.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface QuickDebtProjection extends DebtProjection {

    UUID getUserId();
    String getUserFirstName();
    String getUserLastName();
    String getRole();
    String getTargetUserName();
}
