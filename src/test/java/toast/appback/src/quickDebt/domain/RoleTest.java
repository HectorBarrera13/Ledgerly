package toast.appback.src.quickDebt.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    @Test
    public void roleRoleIsDebtorResultIsSuccessful() {
        var role = Role.DEBTOR;
        assertEquals("Debtor", role.getRole());
    }
}