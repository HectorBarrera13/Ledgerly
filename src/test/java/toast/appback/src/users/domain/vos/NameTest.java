package toast.appback.src.users.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("Name Value Object Test")
public class NameTest {

    @Nested
    @DisplayName("Valid Cases")
    class ValidCases {

        static Stream<String> validNAmesProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "valid_names.txt"));
        }

        @ParameterizedTest
        @MethodSource("validNAmesProvider")
        @DisplayName("Should create Name successfully with valid inputs")
        void shouldCreateNameSuccessfullyWithValidInputs(String fullName) {
            String[] parts = fullName.split(" ", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : null; // Default last name if not provided

            Result<Name, DomainError> result = Name.create(firstName, lastName);
            assertTrue(result.isOk(), "Expected success for valid name: " + fullName);
            Name name = result.get();
            assertEquals(firstName, name.getFirstName());
            assertEquals(lastName, name.getLastName());
        }
    }

    @Nested
    @DisplayName("Invalid Cases")
    class InvalidCases {

        @Test
        @DisplayName("Should fail when Name is empty")
        void shouldFailWhenNameIsEmpty() {
            String emptyName = "";
            Result<Name, DomainError> resultWithFirstNameEmpty = Name.create(emptyName, "ValidLastName");
            assertTrue(resultWithFirstNameEmpty.isFailure());
            assertOnlyErrorExistsForField(resultWithFirstNameEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");

            Result<Name, DomainError> resultWithLastNameEmpty = Name.create("ValidFirstName", emptyName);
            assertTrue(resultWithLastNameEmpty.isFailure());
            assertOnlyErrorExistsForField(resultWithLastNameEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");

            Result<Name, DomainError> resultWithBothNamesEmpty = Name.create(emptyName, emptyName);
            assertTrue(resultWithBothNamesEmpty.isFailure());
            assertEquals(2, resultWithBothNamesEmpty.getErrors().size());
            assertErrorExistsForField(resultWithBothNamesEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");
            assertErrorExistsForField(resultWithBothNamesEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");
        }

        @Test
        @DisplayName("Should fail when Name is only whitespace")
        void shouldFailWhenNameIsOnlyWhitespace() {
            String whitespaceName = "   ";
            Result<Name, DomainError> resultWithFirstNameWhitespace = Name.create(whitespaceName, "ValidLastName");
            assertTrue(resultWithFirstNameWhitespace.isFailure());
            assertOnlyErrorExistsForField(resultWithFirstNameWhitespace.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");

            Result<Name, DomainError> resultWithLastNameWhitespace = Name.create("ValidFirstName", whitespaceName);
            assertTrue(resultWithLastNameWhitespace.isFailure());
            assertOnlyErrorExistsForField(resultWithLastNameWhitespace.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");

            Result<Name, DomainError> resultWithBothNamesWhitespace = Name.create(whitespaceName, whitespaceName);
            assertTrue(resultWithBothNamesWhitespace.isFailure());
            assertEquals(2, resultWithBothNamesWhitespace.getErrors().size());
            assertErrorExistsForField(resultWithBothNamesWhitespace.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");
            assertErrorExistsForField(resultWithBothNamesWhitespace.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");
        }

        @Test
        @DisplayName("Should fail when Name is Null")
        void shouldFailWhenNameIsNull() {
            Result<Name, DomainError> resultWithFirstNameNull = Name.create(null, "ValidLastName");
            assertTrue(resultWithFirstNameNull.isFailure());
            assertOnlyErrorExistsForField(resultWithFirstNameNull.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");

            Result<Name, DomainError> resultWithLastNameNull = Name.create("ValidFirstName", null);
            assertTrue(resultWithLastNameNull.isFailure());
            assertOnlyErrorExistsForField(resultWithLastNameNull.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");

            Result<Name, DomainError> resultWithBothNamesNull = Name.create(null, null);
            assertTrue(resultWithBothNamesNull.isFailure());
            assertEquals(2, resultWithBothNamesNull.getErrors().size());
            assertErrorExistsForField(resultWithBothNamesNull.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");
            assertErrorExistsForField(resultWithBothNamesNull.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");
        }

        @Test
        @DisplayName("Should fail when each member name is too long (over 80 characters)")
        void shouldFailWhenNameIsTooLong() {
            String longName = "A".repeat(101);
            Result<Name, DomainError> resultWithFirstNameTooLong = Name.create(longName, "ValidLastName");
            assertTrue(resultWithFirstNameTooLong.isFailure());
            assertOnlyErrorExistsForField(resultWithFirstNameTooLong.getErrors(), ValidatorType.TOO_LONG, "firstName");

            Result<Name, DomainError> resultWithLastNameTooLong = Name.create("ValidFirstName", longName);
            assertTrue(resultWithLastNameTooLong.isFailure());
            assertOnlyErrorExistsForField(resultWithLastNameTooLong.getErrors(), ValidatorType.TOO_LONG, "lastName");

            Result<Name, DomainError> resultWithBothNamesTooLong = Name.create(longName, longName);
            assertTrue(resultWithBothNamesTooLong.isFailure());
            assertEquals(2, resultWithBothNamesTooLong.getErrors().size());
            assertErrorExistsForField(resultWithBothNamesTooLong.getErrors(), ValidatorType.TOO_LONG, "firstName");
            assertErrorExistsForField(resultWithBothNamesTooLong.getErrors(), ValidatorType.TOO_LONG, "lastName");
        }

        @Test
        @DisplayName("Should fail when each member name is too short (less than 2 characters)")
        void shouldFailWhenNameIsTooShort() {
            String shortName = "A";
            Result<Name, DomainError> resultWithFirstNameTooShort = Name.create(shortName, "ValidLastName");
            assertTrue(resultWithFirstNameTooShort.isFailure());
            assertOnlyErrorExistsForField(resultWithFirstNameTooShort.getErrors(), ValidatorType.TOO_SHORT, "firstName");

            Result<Name, DomainError> resultWithLastNameTooShort = Name.create("ValidFirstName", shortName);
            assertTrue(resultWithLastNameTooShort.isFailure());
            assertOnlyErrorExistsForField(resultWithLastNameTooShort.getErrors(), ValidatorType.TOO_SHORT, "lastName");

            Result<Name, DomainError> resultWithBothNamesTooShort = Name.create(shortName, shortName);
            assertTrue(resultWithBothNamesTooShort.isFailure());
            assertEquals(2, resultWithBothNamesTooShort.getErrors().size());
            assertErrorExistsForField(resultWithBothNamesTooShort.getErrors(), ValidatorType.TOO_SHORT, "firstName");
            assertErrorExistsForField(resultWithBothNamesTooShort.getErrors(), ValidatorType.TOO_SHORT, "lastName");
        }
    }

    @Nested
    @DisplayName("Integration Cases")
    class IntegrationCases {

        static Stream<String> invalidNamesProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "invalid_names.txt"));
        }

        @ParameterizedTest
        @MethodSource("invalidNamesProvider")
        @DisplayName("Should fail to create Name with invalid inputs from file")
        void shouldFailToCreateNameWithInvalidInputsFromFile(String fullName) {
            String[] parts = fullName.split(" ", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : null; // Default last name if not provided

            Result<Name, DomainError> result = Name.create(firstName, lastName);
            assertTrue(result.isFailure(), "Expected failure for invalid name: " + fullName);
        }

        @Test
        @DisplayName("Should get full name correctly")
        void shouldGetFullNameCorrectly() {
            String firstName = "John";
            String lastName = "Doe";
            Result<Name, DomainError> result = Name.create(firstName, lastName);
            assertTrue(result.isOk());
            Name name = result.get();
            assertEquals("John Doe", name.getFullName());
        }

        @Test
        @DisplayName("Should be equal for same first and last names")
        void shouldBeEqualForSameFirstAndLastNames() {
            Result<Name, DomainError> result1 = Name.create("Jane", "Smith");
            Result<Name, DomainError> result2 = Name.create("Jane", "Smith");
            assertTrue(result1.isOk());
            assertTrue(result2.isOk());
            Name name1 = result1.get();
            Name name2 = result2.get();
            assertEquals(name1, name2);
        }

        @Test
        @DisplayName("Should not be equal for different first or last names")
        void shouldNotBeEqualForDifferentFirstOrLastNames() {
            Result<Name, DomainError> result1 = Name.create("Alice", "Johnson");
            Result<Name, DomainError> result2 = Name.create("Alice", "Smith");
            Result<Name, DomainError> result3 = Name.create("Bob", "Johnson");
            assertTrue(result1.isOk());
            assertTrue(result2.isOk());
            assertTrue(result3.isOk());
            Name name1 = result1.get();
            Name name2 = result2.get();
            Name name3 = result3.get();
            assertNotEquals(name1, name2);
            assertNotEquals(name1, name3);
        }
    }
}
