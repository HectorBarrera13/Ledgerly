package toast.appback.src.debts.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.event.DebtRejected;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Debt Aggregate Test")
class DebtBetweenUsersTest {

    // ---------------------------
    // Constantes compartidas
    // ---------------------------

    private static final UserId DEBTOR = UserId.load(UUID.randomUUID());
    private static final UserId CREDITOR = UserId.load(UUID.randomUUID());

    private static final Context CONTEXT =
            Context.load("Cena", "Pago de restaurante");

    private static final DebtMoney MONEY =
            DebtMoney.load(new java.math.BigDecimal("10.00"), "MXN");


    // ---------------------------
    // create()
    // ---------------------------

    @Test
    void create_ShouldInitializePendingState_AndRecordCreationEvent() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        assertEquals(Status.PENDING, debt.getStatus());
        assertEquals(DEBTOR, debt.getDebtorId());
        assertEquals(CREDITOR, debt.getCreditorId());

        var events = debt.pullEvents();
        assertEquals(1, events.size());
    }


    // ---------------------------
    // accept()
    // ---------------------------

    @Test
    void accept_ShouldMoveToAccepted_WhenPending() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.accept();

        assertTrue(result.isOk());
        assertEquals(Status.ACCEPTED, debt.getStatus());

        var events = debt.pullEvents();
        assertTrue(events.get(0) instanceof DebtCreated);
    }

    @Test
    void accept_ShouldFail_WhenNotPending() {
        DebtBetweenUsers debt =
                DebtBetweenUsers.load(DebtId.generate(), CONTEXT, MONEY, DEBTOR, CREDITOR, Status.REJECTED);

        var result = debt.accept();

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // reject()
    // ---------------------------

    @Test
    void reject_ShouldMoveToRejected_WhenPending() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.reject();

        assertTrue(result.isOk());
        assertEquals(Status.REJECTED, debt.getStatus());
    }

    @Test
    void reject_ShouldFail_WhenNotPending() {
        DebtBetweenUsers debt =
                DebtBetweenUsers.load(DebtId.generate(), CONTEXT, MONEY, DEBTOR, CREDITOR, Status.ACCEPTED);

        var result = debt.reject();

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // reportPayment()
    // ---------------------------

    @Test
    void reportPayment_ShouldMoveToPendingConfirmation_WhenAccepted() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);
        debt.accept();

        var result = debt.reportPayment();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMATION_PENDING, debt.getStatus());
    }

    @Test
    void reportPayment_ShouldMoveToPendingConfirmation_WhenPaymentRejectedPreviously() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);
        debt.accept();
        debt.reportPayment();
        debt.rejectPayment();

        var result = debt.reportPayment();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMATION_PENDING, debt.getStatus());
    }

    @Test
    void reportPayment_ShouldFail_WhenNotAccepted() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.reportPayment();

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // confirmPayment()
    // ---------------------------

    @Test
    void confirmPayment_ShouldSetConfirmed_WhenPendingConfirmation() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);
        debt.accept();
        debt.reportPayment();

        var result = debt.confirmPayment();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMED, debt.getStatus());
    }

    @Test
    void confirmPayment_ShouldFail_WhenNotPendingConfirmation() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.confirmPayment();

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // rejectPayment()
    // ---------------------------

    @Test
    void rejectPayment_ShouldSetRejected_WhenPendingConfirmation() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);
        debt.accept();
        debt.reportPayment();

        var result = debt.rejectPayment();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMATION_REJECTED, debt.getStatus());
    }

    @Test
    void rejectPayment_ShouldFail_WhenNotPendingConfirmation() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.rejectPayment();

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // pay()
    // ---------------------------

    @Test
    void pay_ShouldBehaveLikeReportPayment_WhenAccepted() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);
        debt.accept();

        var result = debt.pay();

        assertTrue(result.isOk());
        assertEquals(Status.PAYMENT_CONFIRMATION_PENDING, debt.getStatus());
    }

    @Test
    void pay_ShouldFail_WhenNotAccepted() {
        DebtBetweenUsers debt = DebtBetweenUsers.create(CONTEXT, MONEY, DEBTOR, CREDITOR);

        var result = debt.pay();

        assertTrue(result.isFailure());
    }
}