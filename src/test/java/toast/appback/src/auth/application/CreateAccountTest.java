package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.domain.CreationAccountException;
import toast.appback.src.auth.application.mother.AccountMother;
import toast.appback.src.auth.application.usecase.implementation.CreateAccountUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Create account use case tests")
public class CreateAccountTest {
    private CreateAccountUseCase createAccountUseCase;
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final AccountFactory accountFactory = mock(AccountFactory.class);
    private final String email = "johndoe@gmail.com";

    @BeforeEach
    public void setUp() {
        this.createAccountUseCase = new CreateAccountUseCase(
                accountRepository,
                accountFactory
        );
    }

    @Test
    @DisplayName("Should create an account successfully")
    public void testCreateAccountSuccess() {
        Account account = AccountMother.withEmail(email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(accountFactory.create(any())).thenReturn(Result.ok(account));

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                email,
                "securePassword123"
        );

        CreateAccountResult result = createAccountUseCase.execute(command);
        assertNotNull(result);
        assertEquals(account, result.account());
        assertNotNull(result.session());

        verify(accountRepository, times(1)).findByEmail(email);
        verify(accountFactory, times(1)).create(any());
        verify(accountRepository, times(1)).save(account);

        verifyNoMoreInteractions(
                accountRepository,
                accountFactory
        );
    }

    @Test
    @DisplayName("Should throw AccountExistsException when account with email already exists")
    public void testCreateAccountAccountExists() {
        Account existingAccount = AccountMother.withEmail(email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(existingAccount));

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                email,
                "securePassword123"
        );

        AccountExistsException exception = assertThrows(AccountExistsException.class, () -> {
            createAccountUseCase.execute(command);
        });

        assertEquals(exception.getEmail(), email);

        verify(accountRepository, times(1)).findByEmail(email);

        verifyNoInteractions(
                accountFactory
        );

        verifyNoMoreInteractions(
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw CreationAccountException when account creation fails")
    public void testCreateAccountCreationFails() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(accountFactory.create(any())).thenReturn(Result.failure(DomainError.empty()));

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                email,
                "securePassword123"
        );

        assertThrows(CreationAccountException.class, () -> createAccountUseCase.execute(command));

        verify(accountRepository, times(1)).findByEmail(email);
        verify(accountFactory, times(1)).create(any());

        verifyNoMoreInteractions(
                accountRepository,
                accountFactory
        );
    }
}
