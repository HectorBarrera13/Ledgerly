package toast.appback.src.auth.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.domain.Password;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Value Object Test")
public class PasswordTest {

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

    @Nested
    @DisplayName("Valid Cases")
    class ValidCases {

        @Test
        @DisplayName("Create a valid password")
        void createValidPassword() {
            Result<Password, DomainError> result = Password.fromPlain("Str0ngP@ssw0rd!", passwordHasher);
            assertTrue(result.isSuccess());
            Password password = result.getValue();
            assertTrue(passwordHasher.verify("Str0ngP@ssw0rd!", password.hashed()));
        }
    }

    @Nested
    @DisplayName("Invalid Cases")
    class InvalidCases {
        @Test
        @DisplayName("Fail to create password when too short (less than 8 characters)")
        void failToCreatePasswordWhenTooShort() {
            Result<Password, DomainError> result = Password.fromPlain("Short1!", passwordHasher);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }

        @Test
        @DisplayName("Fail to create password when missing uppercase")
        void failToCreatePasswordWhenMissingUppercase() {
            Result<Password, DomainError> result = Password.fromPlain("nouppercase1!", passwordHasher);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }

        @Test
        @DisplayName("Fail to create password when missing digit")
        void failToCreatePasswordWhenMissingDigit() {
            Result<Password, DomainError> result = Password.fromPlain("NoDigitPass!", passwordHasher);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }
    }
}
