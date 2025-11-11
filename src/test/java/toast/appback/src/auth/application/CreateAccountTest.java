package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.usecase.implementation.CreateAccountUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.middleware.ApplicationException;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateAccountTest {
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final AccountFactory accountFactory = mock(AccountFactory.class);
    private CreateAccountUseCase createAccountUseCase;

    @BeforeEach
    public void setUp() {
        createAccountUseCase = new CreateAccountUseCase(accountRepository, accountFactory);
    }

    /**
     * <p>Test case: Create account successfully
     * <p>Precondition: Valid CreateAccountCommand is provided and no existing account with the same email
     * <p>Expected outcome: Account is created and saved in the repository
     */
    @Test
    @DisplayName("Should create account successfully")
    public void testCreateAccountSuccessfully() {
        Account account = mock(Account.class);
        when(accountFactory.create(any(), any(), any()))
                .thenReturn(Result.success(account));
        when(account.getAccountId()).thenReturn(AccountId.generate());
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "johndoe@gmail.com",
                "securePassword123"
        );
        Account result = createAccountUseCase.execute(command);
        assertEquals(account, result);
        verify(accountRepository, times(1)).save(account);
    }

    /**
     * <p>Test case: Create account fails due to invalid data
     * <p>Precondition: Invalid CreateAccountCommand is provided
     * <p>Expected outcome: {@link ApplicationException} is thrown
     */
    @Test
    @DisplayName("Should fail to create account with invalid data")
    public void testCreateAccountFailsWithInvalidData() {
        String message = "TEST_ERROR";
        when(accountFactory.create(any(), any(), any()))
                .thenReturn(Result.failure(DomainError.businessRule(message)));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "invalid-email",
                "123"
        );
        Exception exception = assertThrows(ApplicationException.class, () -> {
            createAccountUseCase.execute(command);
        });
        assertTrue(exception.getMessage().contains(message));
        verify(accountRepository, never()).save(any());
    }

    /**
     * <p>Test case: Create account fails due to existing email
     * <p>Precondition: CreateAccountCommand is provided with an email that already exists
     * <p>Expected outcome: {@link ApplicationException} is thrown
     */
    @Test
    @DisplayName("Should fail to create account with existing email")
    public void testCreateAccountFailsWithExistingEmail() {
        Account existingAccount = mock(Account.class);
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(existingAccount));
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "johndoe@gmail.com",
                "securePassword123"
        );
        Exception exception = assertThrows(ApplicationException.class, () -> {
            createAccountUseCase.execute(command);
        });
        assertTrue(exception.getMessage().contains("Account with email already exists"));
        verify(accountFactory, never()).create(any(), any(), any());
        verify(accountRepository, never()).save(any());
    }
}
