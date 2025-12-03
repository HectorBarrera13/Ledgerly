package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.implementation.EditDebtBetweenUsersUseCase;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditDebtBetweenUsersTest {

    private DebtRepository debtRepository;
    private DomainEventBus domainEventBus;
    private UserRepository userRepository;

    private EditDebtBetweenUsersUseCase useCase;

    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId DEBTOR_ID = UserId.load(UUID.randomUUID());
    private final UserId CREDITOR_ID = UserId.load(UUID.randomUUID());

    private DebtBetweenUsers debt;
    private User debtorUser;
    private User creditorUser;

    private EditDebtCommand COMMAND;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        domainEventBus = mock(DomainEventBus.class);
        userRepository = mock(UserRepository.class);

        useCase = new EditDebtBetweenUsersUseCase(debtRepository, domainEventBus, userRepository);

        // Debt base
        Context ctx = Context.load("Old", "Old desc");
        DebtMoney money = DebtMoney.load(new BigDecimal("10.00"), "MXN");
        debt = DebtBetweenUsers.load(DEBT_ID, ctx, money, DEBTOR_ID, CREDITOR_ID, Status.PENDING);

        // Comando de edición
        COMMAND = new EditDebtCommand(
                DEBTOR_ID,
                DEBT_ID,
                "Nuevo propósito",
                "Nueva descripción",
                "USD",
                500L
        );

        // Mock usuarios
        debtorUser = mock(User.class);
        when(debtorUser.getUserId()).thenReturn(DEBTOR_ID);
        when(debtorUser.getName()).thenReturn(Name.load("Luis", "Martinez"));

        creditorUser = mock(User.class);
        when(creditorUser.getUserId()).thenReturn(CREDITOR_ID);
        when(creditorUser.getName()).thenReturn(Name.load("Ana", "Diaz"));
    }

    // ---------------------------------------------------------
    // 1) Deuda NO encontrada
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) Fallo al editar con dominio (status != PENDING)
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtCannotBeEdited() {
        // Cambiamos estado para provocar fallo
        DebtBetweenUsers nonEditable =
                DebtBetweenUsers.load(DEBT_ID, Context.load("x","y"),
                        DebtMoney.load(new BigDecimal("1.00"), "MXN"),
                        DEBTOR_ID, CREDITOR_ID, Status.ACCEPTED);

        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(nonEditable));

        // Debtor/creditor
        when(userRepository.findById(DEBTOR_ID)).thenReturn(Optional.of(debtorUser));
        when(userRepository.findById(CREDITOR_ID)).thenReturn(Optional.of(creditorUser));

        assertThrows(EditDebtException.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 4) Deudor no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 5) Acreedor no encontrado
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
    // 6) Éxito total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldEditDebt_Save_AndPublishEvents() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        DebtBetweenUsersView view = useCase.execute(COMMAND);

        assertEquals("Nuevo propósito", view.purpose());
        assertEquals("Nueva descripción", view.description());
        assertEquals("USD", view.currency());
        assertEquals(500L / 100.0, view.amount().doubleValue());

        verify(debtRepository).save(debt);
        verify(domainEventBus).publishAll(any());
    }
}
