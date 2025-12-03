package toast.appback.src.debts.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuickDebt Aggregate Test")
class QuickDebtTest {

    // ---------------------------
    // Constantes
    // ---------------------------

    private static final UserId USER = UserId.load(UUID.randomUUID());
    private static final Role ROLE = Role.load("DEBTOR");
    private static final TargetUser TARGET = TargetUser.load("Carlos");

    private static final Context CONTEXT =
            Context.load("Compra", "Compra rápida en tienda");

    private static final DebtMoney MONEY =
            DebtMoney.load(new BigDecimal("25.50"), "MXN");


    // ---------------------------
    // create()
    // ---------------------------

    @Test
    void create_ShouldInitializeWithPendingStatus_AndRecordCreationEvent() {
        QuickDebt debt = QuickDebt.create(CONTEXT, MONEY, USER, ROLE, TARGET);

        assertEquals(Status.PENDING, debt.getStatus());
        assertEquals(USER, debt.getUserId());
        assertEquals(ROLE, debt.getRole());
        assertEquals(TARGET, debt.getTargetUser());

        var events = debt.pullEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DebtCreated);
    }


    // ---------------------------
    // load()
    // ---------------------------

    @Test
    void load_ShouldRebuildQuickDebt_WithGivenState() {
        DebtId id = DebtId.generate();
        QuickDebt debt = QuickDebt.load(id, CONTEXT, MONEY, USER, ROLE, TARGET, Status.ACCEPTED);

        assertEquals(id, debt.getId());
        assertEquals(Status.ACCEPTED, debt.getStatus());
        assertEquals(USER, debt.getUserId());
    }


    // ---------------------------
    // editTargetUser()
    // ---------------------------

    @Test
    void changeToDebtBetweenUsers_ShouldCreateNewDebtBetweenUsers_KeepingSameCoreData() {
        QuickDebt debt = QuickDebt.create(CONTEXT, MONEY, USER, ROLE, TARGET);

        UserId newUser = UserId.load(UUID.randomUUID());

        DebtBetweenUsers converted = debt.changeToDebtBetweenUsers(newUser);

        assertEquals(debt.getId(), converted.getId());
        assertEquals(debt.getContext(), converted.getContext());
        assertEquals(debt.getDebtMoney(), converted.getDebtMoney());
        assertEquals(debt.getStatus(), converted.getStatus());

        assertEquals(USER, converted.getDebtorId());
        assertEquals(newUser, converted.getCreditorId());
    }


    // ---------------------------
    // pay()
    // ---------------------------

    @Test
    void pay_ShouldConfirmPayment_WhenPending() {
        QuickDebt debt = QuickDebt.create(CONTEXT, MONEY, USER, ROLE, TARGET);

        var result = debt.pay();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMED, debt.getStatus());
    }

    @Test
    void pay_ShouldFail_WhenNotPending() {
        QuickDebt debt = QuickDebt.load(
                DebtId.generate(), CONTEXT, MONEY, USER, ROLE, TARGET, Status.REJECTED
        );

        var result = debt.pay();

        assertTrue(result.isFailure());
    }
}