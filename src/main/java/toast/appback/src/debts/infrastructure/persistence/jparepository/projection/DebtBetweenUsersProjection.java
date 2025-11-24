package toast.appback.src.debts.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface DebtBetweenUsersProjection extends DebtProjection {

    UUID getDebtorId();
    String getDebtorFirstName();
    String getDebtorLastName();

    UUID getCreditorId();
    String getCreditorFirstName();
    String getCreditorLastName();
}
