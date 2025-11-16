package toast.appback.src.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("AccountFactory Domain Tests")
public class AccountFactoryTest {
    private final PasswordHasher passwordHasher = new PasswordHasher() {
        @Override
        public String hash(String rawPassword) {
            return rawPassword + "_hashed";
        }

        @Override
        public boolean verify(String rawPassword, String hashedPassword) {
            return hashedPassword.equals(hash(rawPassword));
        }
    };

    private final AccountFactory accountFactory = new DefaultAccount(passwordHasher);

    @Test
    @DisplayName("Collect all errors when creating a user with invalid data")
    void testCreateUserWithInvalidData() {
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.load(UUID.randomUUID()),
                "invalidEmail.com",
                "1234123"
        );
        Result<Account, DomainError> result = accountFactory.create(command);
        assertFalse(result.isOk(), "Expected successful account creation");
        assertEquals(2, result.getErrors().size(), "Expected 2 validation errors");
    }

    @Test
    @DisplayName("Collect some errors when creating a user with partially invalid data")
    void testCreateUserWithPartiallyInvalidData() {
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.load(UUID.randomUUID()),
                "johndoe@gmail.com",
                "123"
        );
        Result<Account, DomainError> result = accountFactory.create(command);
        assertFalse(result.isOk(), "Expected successful account creation");
        assertEquals(1, result.getErrors().size(), "Expected 1 validation errors");
    }

    @Test
    @DisplayName("Successfully create a user with valid data")
    void testCreateUserWithValidData() {
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.load(UUID.randomUUID()),
                "johndoe@gmail.com",
                "123AWDAAWDW"
        );
        Result<Account, DomainError> result = accountFactory.create(command);
        assertTrue(result.isOk(), "Expected successful account creation");
        Account account = result.get();
        assertEquals("johndoe@gmail.com", account.getEmail().getValue());
        assertTrue(passwordHasher.verify("123AWDAAWDW", account.getPassword().getHashed()), "Password should be hashed correctly");
        assertNotNull(account.getAccountId());
    }

    @Test
    @DisplayName("Should generate a domain event upon user creation")
    void testUserCreationGeneratesDomainEvent() {
        CreateAccountCommand command = new CreateAccountCommand(
                UserId.load(UUID.randomUUID()),
                "johndoe@gmail.com",
                "123F2ASCASD!"
        );
        Result<Account, DomainError> result = accountFactory.create(command);
        assertTrue(result.isOk(), "Expected successful account creation");
        Account account = result.get();
        List<DomainEvent> events = account.pullEvents();
        assertEquals(1, events.size(), "Expected one domain event");
        assertInstanceOf(AccountCreated.class, events.getFirst(), "Expected event to be of type AccountCreated");
    }
}
