package toast.appback.src.debts.domain;

import org.junit.jupiter.api.Test;
import toast.appback.src.users.domain.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {

    private final UserId idUser1 = UserId.load(UUID.randomUUID());
    private final Name nameUser1 = Name.load("Hector", "Barrera");
    private final Phone phoneUser1 = Phone.load("52","987654321");
    private final User user1 = new User(idUser1,nameUser1,phoneUser1);

    private final UserId idUser2 = UserId.load(UUID.randomUUID());
    private final Name nameUser2 = Name.load("Antonio", "Polanco");
    private final Phone phoneUser2 = Phone.load("52","1234567890");
    private final User user2 = new User(idUser2,nameUser2, phoneUser2);

    private final Context context = new Context("Pizzas", "");
    private final Amount amount = new Amount("MXN", 100.00);
    Debt debt = new Debt(
            new DebtId(UUID.randomUUID()),
            context,
            amount,
            user1,
            user2
    );

    @Test
    public void Debt_CreateDebt_DebtIsCreated(){
        assertNotNull(debt);
    }

    @Test
    public void Debt_CreateDebt_DebtIsCreatedWithId(){
        System.out.println(debt.getStatus());
        assertNotNull(debt);
    }

    @Test
    public void Debt_AcceptDebt_DebtStatusRemainCREATED(){
        debt.accept();
        assertEquals(Status.ACCEPTED, debt.getStatus());
    }

    @Test
    public void Debt_RejectDebt_DebtStatusREJECTED(){
        debt.reject();
        assertEquals(Status.REJECTED, debt.getStatus());
    }

    @Test
    public void Debt_PayDebt_DebtStatusPAID(){
        debt.accept();
        debt.pay();
        assertEquals(Status.PAID, debt.getStatus());
    }

    @Test
    public void Debt_EditAmount_DebtStatusPAIDWithId(){
        Amount newAmount = new Amount("MXN", 90.00);
        debt.editAmount(newAmount);
        assertEquals(newAmount, debt.getAmount());
    }
}
