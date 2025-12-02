package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.usecase.implementation.CreateQuickDebtUseCase;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CreateQuickDebtTest {
    private UserRepository userRepository;
    private DebtRepository debtRepository;
    private CreateQuickDebtUseCase useCase;

    private final UserId USER_ID = UserId.load(UUID.randomUUID());

    private final CreateQuickDebtCommand COMMAND =
            new CreateQuickDebtCommand(
                    "Cena",
                    "Pago de comida",
                    "MXN",
                    1000L,
                    USER_ID,
                    "DEBTOR",
                    "Carlos"
            );

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        debtRepository = mock(DebtRepository.class);

        useCase = new CreateQuickDebtUseCase(userRepository, debtRepository);

        // Mock user
        user = mock(User.class);
        when(user.getUserId()).thenReturn(USER_ID);
        when(user.getName()).thenReturn(Name.load("Juan", "Lopez"));
    }

    // ---------------------------------------------------------
    // 1) Usuario no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) Fallo en algún Value Object
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenValueObjectValidationFails() {
        // Campo inválido: currency = "MEXX"
        CreateQuickDebtCommand invalid =
                new CreateQuickDebtCommand(
                        "Cena",
                        "Desc",
                        "MXNN",  // inválida
                        100L,
                        USER_ID,
                        "DEBTOR",
                        "Carlos"
                );

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        assertThrows(CreationDebtException.class,
                () -> useCase.execute(invalid));
    }

    // ---------------------------------------------------------
    // 3) Éxito total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldCreateQuickDebt_Save_AndReturnView() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        QuickDebtView view = useCase.execute(COMMAND);

        // Validar campos del DTO resultante
        assertEquals("Cena", view.purpose());
        assertEquals("Pago de comida", view.description());
        assertEquals(Status.PENDING.toString(), view.status());
        assertEquals("MXN", view.currency());
        assertEquals(USER_ID.getValue(), view.userSummary().userId());
        assertEquals("DEBTOR", view.role());
        assertEquals("Carlos", view.targetUserName());

        // Verificar persistencia
        verify(debtRepository).save(any(QuickDebt.class));
    }
}