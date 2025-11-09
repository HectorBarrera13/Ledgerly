package toast.appback.src.debts.domain;

import org.junit.jupiter.api.Test;
import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {

    private UserId idUser1 = new UserId(UUID.randomUUID());
    private Name nameUser1 = new Name("Hector", "Barrera");
    private Phone phoneUser1 = new Phone("52","987654321");
    private List<Friend> friendsUsaer1 = new ArrayList<>();
    private User user1 = new User(idUser1,nameUser1,phoneUser1,friendsUsaer1);

    private UserId idUser2 = new UserId(UUID.randomUUID());
    private Name nameUser2 = new Name("Antonio", "Polanco");
    private Phone phoneUser2 = new Phone("52","1234567890");
    private List<Friend> friendsUser2 = new ArrayList<>();
    private User user2 = new User(idUser2,nameUser2, phoneUser2, friendsUser2);

    private Context context = new Context("Pizzas", "");
    private Amount amount = new Amount("MXN", 100.00);
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
