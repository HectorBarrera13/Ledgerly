package toast.appback.src.quickDebt.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    @Test
    public void Role_RoleIsDebtor_ResultIsSuccesful(){
        var role = Role.DEBTOR;
        assertEquals(role.getRole(), "DEBTOR");
    }
}
