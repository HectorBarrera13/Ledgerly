package toast.appback.src.quickDebt.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.DebtMoney;
import toast.appback.src.users.domain.*;

import java.util.UUID;

public class QuickDebtTest {
    private final UserId idUser1 = UserId.load(UUID.randomUUID());
    private final Name nameUser1 = Name.load("Hector", "Barrera");
    private final Phone phoneUser1 = Phone.load("52","987654321");
    private final User knownUser = new User(idUser1, nameUser1, phoneUser1);
    private final Role role = Role.DEBTOR;

    private final String unknownUser = "Antonio Polanco";

    private final DebtMoney debtMoney = DebtMoney.load(10000L, "MXN");
    private final Context context = Context.load("Zaaza", "o");

    private final QuickDebt quickDebt = new QuickDebt(QuickDebtId.load(UUID.randomUUID()), context, debtMoney, knownUser, role, unknownUser );

    @Test
    public void QuickDebt_CreateQuickDebt_QuickDebtCreated(){
        assertNotNull(quickDebt);
    }
}
