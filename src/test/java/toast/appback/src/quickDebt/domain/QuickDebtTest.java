package toast.appback.src.quickDebt.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import toast.appback.src.debts.domain.Amount;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.users.domain.*;

import java.util.UUID;

public class QuickDebtTest {
    private final UserId idUser1 = UserId.load(UUID.randomUUID());
    private final Name nameUser1 = Name.load("Hector", "Barrera");
    private final Phone phoneUser1 = Phone.load("52","987654321");
    private final User knownUser = new User(idUser1, nameUser1, phoneUser1);
    private final Role role = Role.DEBTOR;

    private final String unknownUser = "Antonio Polanco";

    private final Amount amount = new Amount("MXN", 100.00);
    private final Context context = new Context("Zaaza", "o");

    private final QuickDebt quickDebt = new QuickDebt(new QuickDebtId(UUID.randomUUID()), context, amount, knownUser, role, unknownUser );

    @Test
    public void QuickDebt_CreateQuickDebt_QuickDebtCreated(){
        assertNotNull(quickDebt);
    }
}
