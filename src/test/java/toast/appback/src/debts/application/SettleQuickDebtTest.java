package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.implementation.SettleQuickDebtUseCase;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.*;
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

public class SettleQuickDebtTest {

    private DebtRepository debtRepository;
    private UserRepository userRepository;
    private DomainEventBus domainEventBus;

    private SettleQuickDebtUseCase useCase;

    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId ACTOR_ID = UserId.load(UUID.randomUUID());

    private EditDebtStatusCommand COMMAND;
    private QuickDebt debt;
    private User actorUser;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        userRepository = mock(UserRepository.class);
        domainEventBus = mock(DomainEventBus.class);

        useCase = new SettleQuickDebtUseCase(debtRepository, userRepository, domainEventBus);

        COMMAND = new EditDebtStatusCommand(DEBT_ID, ACTOR_ID);

        Context ctx = Context.load("TestPurpose", "TestDesc");
        DebtMoney money = DebtMoney.load(new BigDecimal("50.00"), "USD");
        Role role = Role.load("DEBTOR");
        TargetUser targetUser = TargetUser.load("Carlos");

        debt = QuickDebt.load(DEBT_ID, ctx, money, ACTOR_ID, role, targetUser, Status.PENDING);

        actorUser = mock(User.class);
        when(actorUser.getUserId()).thenReturn(ACTOR_ID);
        when(actorUser.getName()).thenReturn(Name.load("John", "Doe"));
    }

    // ---------------------------------------------------------
    // 1) Deuda no encontrada
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtNotFound() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) Usuario no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenUserNotFound() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(ACTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtorNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 3) Éxito total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldPayQuickDebt_Save_AndPublishEvents() {
        when(debtRepository.findQuickDebtById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(ACTOR_ID))
                .thenReturn(Optional.of(actorUser));

        QuickDebtView view = useCase.execute(COMMAND);

        assertEquals(DEBT_ID.getValue(), view.debtId());
        assertEquals("TestPurpose", view.purpose());
        assertEquals("PAYMENT_CONFIRMED", view.status());

        verify(debtRepository).save(debt);
        verify(domainEventBus).publishAll(any());
    }
}
