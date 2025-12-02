package toast.appback.src.debts.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.implementation.CreateDebtBetweenUsersUseCase;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Create debt use case tests")
public class CreateDebtBetweenUsersTest {
    private UserRepository userRepository;
    private DebtRepository debtRepository;
    private DomainEventBus domainEventBus;

    private CreateDebtBetweenUsersUseCase useCase;

    private final UserId DEBTOR_ID = UserId.load(UUID.randomUUID());
    private final UserId CREDITOR_ID = UserId.load(UUID.randomUUID());

    private final CreateDebtBetweenUsersCommand COMMAND =
            new CreateDebtBetweenUsersCommand(
                    "Cena",
                    "Pago por alimentos",
                    "MXN",
                    1500L,
                    DEBTOR_ID,
                    CREDITOR_ID
            );

    private User debtorUser;
    private User creditorUser;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        debtRepository = mock(DebtRepository.class);
        domainEventBus = mock(DomainEventBus.class);

        useCase = new CreateDebtBetweenUsersUseCase(userRepository, debtRepository, domainEventBus);

        // Mock User: Deudor
        debtorUser = mock(User.class);
        when(debtorUser.getUserId()).thenReturn(DEBTOR_ID);
        when(debtorUser.getName()).thenReturn(
                Name.load("Juan", "Lopez")
        );

        // Mock User: Acreedor
        creditorUser = mock(User.class);
        when(creditorUser.getUserId()).thenReturn(CREDITOR_ID);
        when(creditorUser.getName()).thenReturn(
                Name.load("Ana", "Perez")
        );
    }

    // ---------------------------------------------------------
    // 1) Debtor no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenDebtorNotFound() {
        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtorNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 2) Creditor no encontrado
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenCreditorNotFound() {
        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CreditorNotFound.class,
                () -> useCase.execute(COMMAND));
    }

    // ---------------------------------------------------------
    // 3) Fallo en Context o DebtMoney (VO)
    // ---------------------------------------------------------

    @Test
    void execute_ShouldThrow_WhenValueObjectValidationFails() {
        CreateDebtBetweenUsersCommand invalidCommand =
                new CreateDebtBetweenUsersCommand(
                        "", // purpose inválido
                        "desc",
                        "MXN",
                        100L,
                        DEBTOR_ID,
                        CREDITOR_ID
                );

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        assertThrows(CreationDebtException.class,
                () -> useCase.execute(invalidCommand));
    }

    // ---------------------------------------------------------
    // 4) Éxito total
    // ---------------------------------------------------------

    @Test
    void execute_ShouldCreateDebt_Save_AndPublishEvents() {
        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        DebtBetweenUsersView view = useCase.execute(COMMAND);

        // Verificación del DTO resultante
        assertEquals("Cena", view.purpose());
        assertEquals("MXN", view.currency());
        assertEquals(DEBTOR_ID.getValue(), view.debtorSummary().userId());
        assertEquals(CREDITOR_ID.getValue(), view.creditorSummary().userId());
        assertEquals(Status.PENDING.toString(), view.status());

        // Verificar que la deuda fue guardada
        verify(debtRepository).save(any(DebtBetweenUsers.class));

        // Verificar publicación de eventos
        verify(domainEventBus).publishAll(any());
    }
}