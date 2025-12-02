package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.implementation.RejectDebtPaymentUseCase;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RejectDebtPaymentTest {

    private DebtRepository debtRepository;
    private UserRepository userRepository;
    private DomainEventBus domainEventBus;

    private RejectDebtPaymentUseCase useCase;

    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId DEBTOR_ID = UserId.load(UUID.randomUUID());
    private final UserId CREDITOR_ID = UserId.load(UUID.randomUUID());

    private EditDebtStatusCommand COMMAND;
    private DebtBetweenUsers debt;
    private User debtorUser;
    private User creditorUser;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        userRepository = mock(UserRepository.class);
        domainEventBus = mock(DomainEventBus.class);

        useCase = new RejectDebtPaymentUseCase(debtRepository, userRepository, domainEventBus);

        COMMAND = new EditDebtStatusCommand(DEBT_ID, CREDITOR_ID);

        Context ctx = Context.load("Purpose", "Desc");
        DebtMoney money = DebtMoney.load(new BigDecimal("10.00"), "MXN");
        debt = DebtBetweenUsers.load(
                DEBT_ID, ctx, money, DEBTOR_ID, CREDITOR_ID, Status.PAYMENT_CONFIRMATION_PENDING
        );

        debtorUser = mock(User.class);
        when(debtorUser.getUserId()).thenReturn(DEBTOR_ID);
        when(debtorUser.getName()).thenReturn(Name.load("Mario", "Lopez"));

        creditorUser = mock(User.class);
        when(creditorUser.getUserId()).thenReturn(CREDITOR_ID);
        when(creditorUser.getName()).thenReturn(Name.load("Laura", "Diaz"));
    }

    // ---------------------------------------------------------
    // 1) Deuda no encontrada
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class, () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) Actor NO es acreedor
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenActorIsNotCreditor() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        EditDebtStatusCommand wrongActor =
                new EditDebtStatusCommand(DEBT_ID, UserId.load(UUID.randomUUID()));

        assertThrows(UnauthorizedActionException.class,
                () -> useCase.execute(wrongActor));
    }

    // ---------------------------------------------------------
    // 3) Debtor no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtorNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 4) Creditor no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenCreditorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 5) Éxito total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldRejectPayment_Save_AndPublishEvents() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        DebtBetweenUsersView view = useCase.execute(COMMAND);

        assertEquals(Status.PAYMENT_CONFIRMATION_REJECTED.toString(), view.status());
        assertEquals(DEBT_ID.getValue(), view.debtId());
        assertEquals("Laura", view.creditorSummary().userFirstName());
        assertEquals("Mario", view.debtorSummary().userFirstName());

        verify(debtRepository).save(debt);
        verify(domainEventBus).publishAll(any());
    }
}
