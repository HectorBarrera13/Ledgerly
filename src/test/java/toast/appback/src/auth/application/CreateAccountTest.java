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
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Create account use case tests")
class CreateAccountTest {
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final PasswordHasher passwordHasher = new AccountMother.FakePasswordHasher();
    private final String email = "johndoe@gmail.com";
    private CreateAccountUseCase createAccountUseCase;

    @BeforeEach
    void setUp() {
        this.createAccountUseCase = new CreateAccountUseCase(
                accountRepository,
                passwordHasher
        );
    }

    @Test
    @DisplayName("Should create an account successfully")
    void testCreateAccountSuccess() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                email,
                "securePassword123"
        );

        CreateAccountResult result = createAccountUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.account());
        assertNotNull(result.session());
        assertEquals(result.account().getEmail().getValue(), email);

        verify(accountRepository, times(1)).findByEmail(email);
        verify(accountRepository, times(1)).save(result.account());

        verifyNoMoreInteractions(
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw AccountExistsException when account with email already exists")
    void testCreateAccountAccountExists() {
        Account existingAccount = AccountMother.withEmail(email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(existingAccount));

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                email,
                "securePassword123"
        );

        AccountExistsException exception = assertThrows(AccountExistsException.class, () -> createAccountUseCase.execute(command));

        assertEquals(exception.getEmail(), email);

        verify(accountRepository, times(1)).findByEmail(email);

        verifyNoMoreInteractions(
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw CreationAccountException when account creation fails")
    void testCreateAccountCreationFails() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        CreateAccountCommand command = new CreateAccountCommand(
                UserId.generate(),
                "",
                "securePassword123"
        );

        assertThrows(CreationAccountException.class, () -> createAccountUseCase.execute(command));

        verify(accountRepository, times(1)).findByEmail("");

        verifyNoMoreInteractions(
                accountRepository
        );
    }
}
