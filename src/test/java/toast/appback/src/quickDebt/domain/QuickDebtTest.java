package toast.appback.src.quickDebt.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import toast.appback.src.debts.domain.Amount;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.users.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuickDebtTest {
    private UserId idUser1 = new UserId(UUID.randomUUID());
    private Name nameUser1 = new Name("Hector", "Barrera");
    private Phone phoneUser1 = new Phone("52","987654321");
    private List<Friend> friendsUsaer1 = new ArrayList<>();
    private User knownUser = new User(idUser1,nameUser1,phoneUser1,friendsUsaer1);
    private Role role = Role.DEBTOR;

    private String unknownUser = "Antonio Polanco";

    private Amount amount = new Amount("MXN", 100.00);
    private Context context = new Context("Zaaza", "o");

    private QuickDebt quickDebt = new QuickDebt(new QuickDebtId(UUID.randomUUID()), context, amount, knownUser, role, unknownUser );

    @Test
    public void QuickDebt_CreateQuickDebt_QuickDebtCreated(){
        assertNotNull(quickDebt);
    }
}
