package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.implementation.EditQuickDebtUseCase;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.*;
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

public class EditQuickDebtTest {

    private DebtRepository debtRepository;
    private DomainEventBus domainEventBus;
    private UserRepository userRepository;
    private EditQuickDebtUseCase useCase;

    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId USER_ID = UserId.load(UUID.randomUUID());

    private QuickDebt debt;
    private User user;

    private EditDebtCommand COMMAND;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        domainEventBus = mock(DomainEventBus.class);
        userRepository = mock(UserRepository.class);

        useCase = new EditQuickDebtUseCase(debtRepository, domainEventBus, userRepository);

        // QuickDebt base en estado PENDING
        Context ctx = Context.load("Old purpose", "Old description");
        DebtMoney money = DebtMoney.load(new BigDecimal("50.00"), "MXN");
        Role role = Role.load("DEBTOR");
        TargetUser targetUser = TargetUser.load("Carlos");

        debt = QuickDebt.load(DEBT_ID, ctx, money, USER_ID, role, targetUser, Status.PENDING);

        COMMAND = new EditDebtCommand(
                USER_ID,
                DEBT_ID,
                "Nuevo propósito",
                "Nueva descripción",
                "USD",
                2000L
        );

        user = mock(User.class);
        when(user.getUserId()).thenReturn(USER_ID);
        when(user.getName()).thenReturn(Name.load("Juan", "Lopez"));
    }

    // ---------------------------------------------------------
    // 1) Deuda NO encontrada
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenQuickDebtNotFound() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) VOs inválidos
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenValueObjectsAreInvalid() {
        EditDebtCommand invalid =
                new EditDebtCommand( USER_ID ,DEBT_ID, "", "desc", "MXN", 100L); // purpose vacío

        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        assertThrows(EditDebtException.class,
                () -> useCase.execute(invalid));
    }

    // ---------------------------------------------------------
    // 3) Error de dominio: no se puede editar (STATUS != PENDING)
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDomainEditFails() {

        QuickDebt nonEditable = QuickDebt.load(
                DEBT_ID,
                Context.load("X", "Y"),
                DebtMoney.load(new BigDecimal("10.00"), "MXN"),
                USER_ID,
                Role.load("DEBTOR"),
                TargetUser.load("Roberto"),
                Status.ACCEPTED // ← estado NO editable
        );

        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(nonEditable));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        assertThrows(EditDebtException.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 4) Usuario no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenUserNotFound() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 5) ÉXITO total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldEditQuickDebt_Save_AndReturnDTO() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        QuickDebtView view = useCase.execute(COMMAND);

        assertEquals("Nuevo propósito", view.purpose());
        assertEquals("Nueva descripción", view.description());
        assertEquals("USD", view.currency());
        assertEquals(Status.PENDING.name(), view.status()); // QuickDebt mantiene PENDING al editar

        verify(debtRepository).save(debt);
        verify(domainEventBus).publishAll(any());
    }
}
