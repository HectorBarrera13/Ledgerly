package toast.appback.src.auth.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.shared.types.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Value Object Test")
public class EmailTest {

    @Nested
    @DisplayName("Valid Cases")
    class ValidCases {

        static Stream<String> validEmailsProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "valid_emails.txt"));
        }

        @ParameterizedTest
        @MethodSource("validEmailsProvider")
        @DisplayName("Should create Email successfully with valid inputs")
        void shouldCreateEmailSuccessfullyWithValidInputs(String email) {
            Result<Email, DomainError> result = Email.create(email);
            assertTrue(result.isSuccess(), "Expected success for valid email: " + email);
            Email createdEmail = result.getValue();
            assertEquals(email, createdEmail.value());
        }
    }

    @Nested
    @DisplayName("Invalid Cases")
    class InvalidCases {
        @Test
        @DisplayName("Should fail when Email is null or empty")
        void shouldFailWhenEmailIsNullOrEmpty() {
            Result<Email, DomainError> nullEmailResult = Email.create(null);
            assertTrue(nullEmailResult.isFailure());
            assertEquals(1, nullEmailResult.getErrors().size());

            Result<Email, DomainError> emptyEmailResult = Email.create("");
            assertTrue(emptyEmailResult.isFailure());
            assertEquals(1, emptyEmailResult.getErrors().size());
        }

        @Test
        @DisplayName("Should fail when Email is only whitespace")
        void shouldFailWhenEmailIsOnlyWhitespace() {
            Result<Email, DomainError> whitespaceEmailResult = Email.create("   ");
            assertTrue(whitespaceEmailResult.isFailure());
            assertEquals(1, whitespaceEmailResult.getErrors().size());
        }

        @Test
        @DisplayName("Should fail when Email format is invalid")
        void shouldFailWhenEmailFormatIsInvalid() {
            String invalidEmail = "invalid-email-format";
            Result<Email, DomainError> result = Email.create(invalidEmail);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }

        @Test
        @DisplayName("Should fail when Email domain is missing")
        void shouldFailWhenEmailDomainIsMissing() {
            String invalidEmail = "user@";
            Result<Email, DomainError> result = Email.create(invalidEmail);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }

        @Test
        @DisplayName("Should fail when Email local part is missing")
        void shouldFailWhenEmailLocalPartIsMissing() {
            String invalidEmail = "@domain.com";
            Result<Email, DomainError> result = Email.create(invalidEmail);
            assertTrue(result.isFailure());
            assertEquals(1, result.getErrors().size());
        }
    }

    @Nested
    @DisplayName("Integration Cases")
    class IntegrationCases {

        static Stream<String> invalidEmailsProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "invalid_emails.txt"));
        }

        @ParameterizedTest
        @MethodSource("invalidEmailsProvider")
        @DisplayName("Should fail to create Email with invalid inputs")
        void shouldFailToCreateEmailWithInvalidInputs(String email) {
            Result<Email, DomainError> result = Email.create(email);
            assertTrue(result.isFailure(), "Expected failure for invalid email: " + email);
            assertEquals(1, result.getErrors().size());
        }

        @Test
        @DisplayName("Should get complete email value")
        void shouldGetCompleteEmailValue() {
            String email = "example@gmail.com";
            Result<Email, DomainError> result = Email.create(email);
            assertTrue(result.isSuccess());
            Email createdEmail = result.getValue();
            assertEquals(email, createdEmail.value());
        }

        @Test
        @DisplayName("Should be equal for same email values")
        void shouldBeEqualForSameEmailValues() {
            String email = "joshua@gmail.com";
            Result<Email, DomainError> result1 = Email.create(email);
            Result<Email, DomainError> result2 = Email.create(email);
            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());
            Email email1 = result1.getValue();
            Email email2 = result2.getValue();
            assertEquals(email1, email2);
        }

        @Test
        @DisplayName("Should not be equal for different email values")
        void shouldNotBeEqualForDifferentEmailValues() {
            Result<Email, DomainError> result1 = Email.create("joshua@gmail.com");
            Result<Email, DomainError> result2 = Email.create("perla@gmail.com");
            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());
            Email email1 = result1.getValue();
            Email email2 = result2.getValue();
            assertNotEquals(email1, email2);
        }
    }
}
