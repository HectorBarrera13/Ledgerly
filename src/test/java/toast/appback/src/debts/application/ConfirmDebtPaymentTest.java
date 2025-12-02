package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.implementation.ConfirmDebtPaymentUseCase;
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

@DisplayName("Confirm Debt Payment Use Case Tests")
class ConfirmDebtPaymentTest {

    private DebtRepository debtRepository;
    private UserRepository userRepository;
    private DomainEventBus domainEventBus;
    private ConfirmDebtPaymentUseCase useCase;

    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId DEBTOR_ID = UserId.load(UUID.randomUUID());
    private final UserId CREDITOR_ID = UserId.load(UUID.randomUUID());

    private final EditDebtStatusCommand COMMAND =
            new EditDebtStatusCommand(DEBT_ID, CREDITOR_ID); // actor = creditor

    private DebtBetweenUsers debt;
    private User debtorUser;
    private User creditorUser;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        userRepository = mock(UserRepository.class);
        domainEventBus = mock(DomainEventBus.class);

        useCase = new ConfirmDebtPaymentUseCase(debtRepository, userRepository, domainEventBus);

        // Entidad del dominio
        Context ctx = Context.load("P", "D");
        DebtMoney money = DebtMoney.load(new BigDecimal("10.00"), "MXN");

        // Solo se puede "confirmar" si está en PAYMENT_CONFIRMATION_PENDING
        debt = DebtBetweenUsers.load(DEBT_ID, ctx, money, DEBTOR_ID, CREDITOR_ID, Status.PAYMENT_CONFIRMATION_PENDING);

        // Mocks de Usuario
        debtorUser = mock(User.class);
        when(debtorUser.getUserId()).thenReturn(DEBTOR_ID);
        when(debtorUser.getName()).thenReturn(Name.load("Juan", "Lopez"));

        creditorUser = mock(User.class);
        when(creditorUser.getUserId()).thenReturn(CREDITOR_ID);
        when(creditorUser.getName()).thenReturn(Name.load("Ana", "Perez"));
    }

    // ---------------------------------------
    // 1) Deuda no encontrada
    // ---------------------------------------
    @Test
    void execute_ShouldThrow_WhenDebtNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------
    // 2) Actor NO es el acreedor
    // ---------------------------------------
    @Test
    void execute_ShouldThrow_WhenActorIsNotCreditor() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        EditDebtStatusCommand invalid =
                new EditDebtStatusCommand(DEBT_ID, UserId.load(UUID.randomUUID()));

        assertThrows(UnauthorizedActionException.class,
                () -> useCase.execute(invalid));
    }

    // ---------------------------------------
    // 3) Deudor no encontrado
    // ---------------------------------------
    @Test
    void execute_ShouldThrow_WhenDebtorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtorNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------
    // 4) Acreedor no encontrado
    // ---------------------------------------
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

    // ---------------------------------------
    // 5) Éxito total
    // ---------------------------------------
    @Test
    void execute_ShouldConfirmPayment_Save_AndPublishEvents() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        DebtBetweenUsersView view = useCase.execute(COMMAND);

        assertEquals(Status.PAYMENT_CONFIRMED.toString(), view.status());
        assertEquals(DEBT_ID.getValue(), view.debtId());

        // Guardar cambios
        verify(debtRepository).save(debt);

        // Publicar eventos
        verify(domainEventBus).publishAll(any());
    }
}
