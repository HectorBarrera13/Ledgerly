package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.domain.CreationAccountException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.usecase.implementation.CreateAccountUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Session;
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

    @BeforeEach
    public void setUp() {
        this.createAccountUseCase = new CreateAccountUseCase(
                accountRepository,
                accountFactory
        );
    }

    /**
     * <p>Test case: Create account successfully
     * <p>Precondition: Valid CreateAccountCommand is provided and no existing account with the same email
     * <p>Expected outcome: Account is created and saved in the repository
     */
    @Test
    @DisplayName("Should create account successfully")
    public void testCreateAccountSuccessfully() {
        // No existing account with the same email
        when(accountRepository.findByEmail(any())).thenReturn(Optional.empty());
        // Mock account creation
        Account account = mock(Account.class);
        when(accountFactory.create(any()))
                .thenReturn(Result.success(account));
        // Mocking equals for assertion
        when(account.getAccountId()).thenReturn(AccountId.generate());
        when(account.startSession()).thenReturn(Result.success(Session.create()));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "johndoe@gmail.com",
                "securePassword123"
        );

        CreateAccountResult result = createAccountUseCase.execute(command);
        assertNotNull(result);
        assertEquals(account, result.account());
        verify(accountFactory, times(1)).create(any());
        verify(accountRepository, times(1)).findByEmail(any());
        verify(accountRepository, times(1)).save(account);
    }

    /**
     * <p>Test case: Create account fails due to invalid data
     * <p>Precondition: Invalid CreateAccountCommand is provided
     * <p>Expected outcome: {@link CreationAccountException} is thrown
     */
    @Test
    @DisplayName("Should fail to create account with invalid data")
    public void testCreateAccountFailsWithInvalidData() {
        String message = "TEST_ERROR";
        // No existing account with the same email
        when(accountRepository.findByEmail(any())).thenReturn(Optional.empty());
        // Mock account creation failure
        when(accountFactory.create(any()))
                .thenReturn(Result.failure(DomainError.businessRule(message)));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "invalid-email",
                "123"
        );

        assertThrows(CreationAccountException.class, () -> createAccountUseCase.execute(command));
        verify(accountRepository, never()).save(any());
        verify(accountFactory, times(1)).create(any());
        verify(accountRepository, times(1)).findByEmail(any());
        verify(accountRepository, never()).save(any());
    }

    /**
     * <p>Test case: Create account fails due to existing email
     * <p>Precondition: CreateAccountCommand is provided with an email that already exists
     * <p>Expected outcome: {@link AccountExistsException} is thrown
     */
    @Test
    @DisplayName("Should fail to create account with existing email")
    public void testCreateAccountFailsWithExistingEmail() {
        // Mock existing account
        Account existingAccount = mock(Account.class);
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(existingAccount));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "johndoe@gmail.com",
                "securePassword123"
        );
        assertThrows(AccountExistsException.class, () -> createAccountUseCase.execute(command));
        verify(accountFactory, never()).create(any());
        verify(accountRepository, never()).save(any());
        verify(accountRepository, times(1)).findByEmail(any());
    }

    /**
     * <p>Test case: Create account fails due to session start failure
     * <p>Precondition: Valid CreateAccountCommand is provided but session start fails
     * <p>Expected outcome: {@link SessionStartException} is thrown
     */
    @Test
    @DisplayName("Should fail to create account when session start fails")
    public void testCreateAccountFailsWhenSessionStartFails() {
        // No existing account with the same email
        when(accountRepository.findByEmail(any())).thenReturn(Optional.empty());
        // Mock account creation
        Account account = mock(Account.class);
        when(accountFactory.create(any()))
                .thenReturn(Result.success(account));
        // Mock session start failure
        when(account.startSession())
                .thenReturn(Result.failure(DomainError.businessRule("SESSION_START_ERROR")));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "johndoe@gmail.com",
                "securePassword123"
        );
        assertThrows(SessionStartException.class, () -> createAccountUseCase.execute(command));
        verify(accountFactory, times(1)).create(any());
        verify(accountRepository, times(1)).findByEmail(any());
        verify(accountRepository, never()).save(any());
    }
}
