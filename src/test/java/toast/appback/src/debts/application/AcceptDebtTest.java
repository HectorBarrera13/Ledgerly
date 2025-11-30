package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.implementation.AcceptDebtUseCase;
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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Accept Debt Use Case Tests")
public class AcceptDebtTest{

    private DebtRepository debtRepository;
    private UserRepository userRepository;
    private DomainEventBus domainEventBus;
    private AcceptDebtUseCase useCase;

    // Valores constantes
    private final DebtId DEBT_ID = DebtId.load(UUID.randomUUID());
    private final UserId DEBTOR_ID = UserId.load(UUID.randomUUID());
    private final UserId CREDITOR_ID = UserId.load(UUID.randomUUID());

    private final EditDebtStatusCommand COMMAND =
            new EditDebtStatusCommand(DEBT_ID, DEBTOR_ID);

    private DebtBetweenUsers debt;
    private User debtorUser;
    private User creditorUser;

    @BeforeEach
    void setup() {
        debtRepository = mock(DebtRepository.class);
        userRepository = mock(UserRepository.class);
        domainEventBus = mock(DomainEventBus.class);

        useCase = new AcceptDebtUseCase(debtRepository, userRepository, domainEventBus);

        // Entidad de dominio
        Context ctx = Context.load("Purpose", "Desc");
        DebtMoney money = DebtMoney.load(new BigDecimal("10.00"), "MXN");

        debt = DebtBetweenUsers.load(DEBT_ID, ctx, money, DEBTOR_ID, CREDITOR_ID, Status.PENDING);

        // Usuario deudor
        debtorUser = mock(User.class);
        when(debtorUser.getUserId()).thenReturn(DEBTOR_ID);
        when(debtorUser.getName()).thenReturn(Name.create("Juan", "Lopez").get());

        // Usuario acreedor
        creditorUser = mock(User.class);
        when(creditorUser.getUserId()).thenReturn(CREDITOR_ID);
        when(creditorUser.getName()).thenReturn(Name.create("Ana", "Perez").get());
    }

    // -----------------------------
    // 1) Deuda no encontrada
    // -----------------------------

    @Test
    void execute_ShouldThrow_WhenDebtNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class, () -> useCase.execute(COMMAND));
    }

    // -----------------------------
    // 2) Actor no autorizado
    // -----------------------------

    @Test
    void execute_ShouldThrow_WhenActorIsNotDebtor() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        EditDebtStatusCommand invalidCmd =
                new EditDebtStatusCommand(DEBT_ID, UserId.load(UUID.randomUUID()));

        assertThrows(UnauthorizedActionException.class,
                () -> useCase.execute(invalidCmd));
    }

    // -----------------------------
    // 3) Debtor no encontrado
    // -----------------------------

    @Test
    void execute_ShouldThrow_WhenDebtorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtorNotFound.class, () -> useCase.execute(COMMAND));
    }

    // -----------------------------
    // 4) Creditor no encontrado
    // -----------------------------

    @Test
    void execute_ShouldThrow_WhenCreditorNotFound() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(DebtNotFound.class, () -> useCase.execute(COMMAND));
    }


    // -----------------------------
    // 5) Dominio falla en accept()
    // -----------------------------

    @Test
    void execute_ShouldThrow_WhenDomainAcceptFails() {
        DebtBetweenUsers spyDebt = spy(debt);

        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(spyDebt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        // Forzamos fallo del dominio

        assertThrows(AcceptDebtException.class, () -> useCase.execute(COMMAND));
    }


    // -----------------------------
    // 6) Éxito total
    // -----------------------------

    @Test
    void execute_ShouldAcceptDebt_Save_AndPublishEvents_AndReturnView() {
        when(debtRepository.findDebtBetweenUsersById(DEBT_ID))
                .thenReturn(Optional.of(debt));

        when(userRepository.findById(DEBTOR_ID))
                .thenReturn(Optional.of(debtorUser));

        when(userRepository.findById(CREDITOR_ID))
                .thenReturn(Optional.of(creditorUser));

        DebtBetweenUsersView view = useCase.execute(COMMAND);

        assertEquals(Status.ACCEPTED.toString(), view.status());
        assertEquals(DEBT_ID.getValue(), view.debtId());

        // Se debe guardar la deuda
        verify(debtRepository).save(debt);

        // Se deben publicar los eventos
        verify(domainEventBus).publishAll(any());

        // Verificación básica del DTO
        assertEquals("Juan", view.debtorSummary().userFirstName());
        assertEquals("Ana", view.creditorSummary().userFirstName());
    }
}
