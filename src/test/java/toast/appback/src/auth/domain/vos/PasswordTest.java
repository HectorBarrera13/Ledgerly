package toast.appback.src.auth.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.domain.Password;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.result.Result;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static toast.appback.src.shared.ValueObjectsUtils.assertErrorExists;
import static toast.appback.src.shared.ValueObjectsUtils.assertOnlyErrorExists;

@DisplayName("Password Value Object Test")
class PasswordTest {

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
            assertTrue(result.isOk());
            Password password = result.get();
            assertTrue(passwordHasher.verify("Str0ngP@ssw0rd!", password.getHashed()));
        }
    }

    @Nested
    @DisplayName("Invalid Cases")
    class InvalidCases {

        @Test
        @DisplayName("Fail to create password when null")
        void failToCreatePasswordWhenNull() {
            Result<Password, DomainError> result = Password.fromPlain(null, passwordHasher);
            assertTrue(result.isFailure());
            assertOnlyErrorExists(result.getErrors(), ValidatorType.EMPTY_VALUE);
        }

        @Test
        @DisplayName("Fail to create password when empty")
        void failToCreatePasswordWhenEmpty() {
            Result<Password, DomainError> result = Password.fromPlain("", passwordHasher);
            assertTrue(result.isFailure());
            assertOnlyErrorExists(result.getErrors(), ValidatorType.EMPTY_VALUE);
        }

        @Test
        @DisplayName("Fail to create password when too short (less than 8 characters)")
        void failToCreatePasswordWhenTooShort() {
            Result<Password, DomainError> result = Password.fromPlain("Short1!", passwordHasher);
            assertTrue(result.isFailure());
            assertErrorExists(result.getErrors(), ValidatorType.TOO_SHORT);
        }

        @Test
        @DisplayName("Fail to create password when missing uppercase")
        void failToCreatePasswordWhenMissingUppercase() {
            Result<Password, DomainError> result = Password.fromPlain("nouppercase1!", passwordHasher);
            assertTrue(result.isFailure());
            assertErrorExists(result.getErrors(), ValidatorType.INVALID_FORMAT);
        }

        @Test
        @DisplayName("Fail to create password when missing digit")
        void failToCreatePasswordWhenMissingDigit() {
            Result<Password, DomainError> result = Password.fromPlain("NoDigitPass!", passwordHasher);
            assertTrue(result.isFailure());
            assertErrorExists(result.getErrors(), ValidatorType.INVALID_FORMAT);
        }
    }
}
