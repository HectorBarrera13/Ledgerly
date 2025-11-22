package toast.appback.src.debts.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.implementation.CreateDebtBetweenUsersUseCase;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Create debt use case tests")
public class CreateDebtBetweenUsersTest {
    private CreateDebtBetweenUsersUseCase createDebtBetweenUsersUseCase;
    private final DebtRepository debtRepository = mock(DebtRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final DomainEventBus domainEventBus = mock(DomainEventBus.class);

    private UserId debtorId;
    private UserId creditorId;
    private User debtor;
    private User creditor;
    private CreateDebtBetweenUsersCommand command;

    @BeforeEach
    void setUp() {
        this.createDebtBetweenUsersUseCase = new CreateDebtBetweenUsersUseCase(
                userRepository,
                debtRepository,
                domainEventBus
        );
        Name name = mock(Name.class);
        Phone phone = mock(Phone.class);

        debtor = User.create(name, phone);
        creditor = User.create(name,phone);

        debtorId   = debtor.getUserId();
        creditorId = creditor.getUserId();

        doNothing().when(debtRepository).save(any(Debt.class));

        command = new CreateDebtBetweenUsersCommand(
                "Cena",
                "Pago de las pizzas",
                "MXN",
                10000L,
                debtorId,
                creditorId
        );
    }

//    @Test
//    @DisplayName("Should create debt succesfully")
//    void createDebt() {
//        when(userRepository.findById(eq(debtorId))).thenReturn(Optional.of(debtor));
//        when(userRepository.findById(eq(creditorId))).thenReturn(Optional.of(creditor));
//
//        DebtView debtView = createDebtBetweenUsersUseCase.execute(command);
//
//        assertEquals("Cena", debtView.purpose());
//        assertEquals("Pago de las pizzas", debtView.description());
//        assertEquals("MXN", debtView.currency());
//        assertEquals(10000L, debtView.amount()*100);
//        assertEquals(debtor.getName().toString(), debtView.debtorName());
//        assertEquals(creditor.getName().toString(), debtView.creditorName());
//
//        verify(userRepository).findById(debtorId);
//        verify(userRepository).findById(creditorId);
//        verify(debtRepository).save(any(Debt.class));
//    }

    @Test
    @DisplayName("Fails when debtor not found")
    void createDebtDebtorNotFound() {
        when(userRepository.findById(eq(debtorId))).thenReturn(Optional.empty());
        when(userRepository.findById(eq(creditorId))).thenReturn(Optional.of(creditor));

        assertThrows(DebtorNotFound.class, () -> createDebtBetweenUsersUseCase.execute(command));
        verify(debtRepository, never()).save(any(Debt.class));
    }

    @Test
    @DisplayName("Fails when Creditor not found")
    void createDebtCreditorNotFound() {
        when(userRepository.findById(eq(debtorId))).thenReturn(Optional.of(debtor));
        when(userRepository.findById(eq(creditorId))).thenReturn(Optional.empty());

        assertThrows(CreditorNotFound.class, () -> createDebtBetweenUsersUseCase.execute(command));
        verify(debtRepository, never()).save(any(Debt.class));
    }


}
