package toast.appback.src.users.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.Phone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("Phone Value Object Test")
public class PhoneTest {

    @Nested
    @DisplayName("Valid Cases")
    class ValidCases {

        static Stream<String> validPhonesProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "valid_phones.txt"));
        }

        @ParameterizedTest
        @MethodSource("validPhonesProvider")
        @DisplayName("Should create Phone successfully with valid inputs")
        void shouldCreatePhoneSuccessfullyWithValidInputs(String fullPhone) {
            String[] parts = fullPhone.split(" ", 2);
            String countryCode = parts[0];
            String number = parts.length > 1 ? parts[1] : null;

            Result<Phone, DomainError> result = Phone.create(countryCode, number);

            assertTrue(result.isSuccess(), "Expected success for valid phone: " + fullPhone);
            Phone phone = result.getValue();
            assertEquals(countryCode, phone.getCountryCode());
            assertEquals(number, phone.getNumber());
            assertEquals(countryCode + "-" + number, phone.getValue());
        }
    }

    @Nested
    @DisplayName("Invalid Cases")
    class InvalidCases {

        @Test
        @DisplayName("Should fail when country code or number is null")
        void shouldFailWhenPhonePartsAreNull() {
            Result<Phone, DomainError> nullCountry = Phone.create(null, "5544332211");
            assertTrue(nullCountry.isFailure());
            assertErrorExists(nullCountry.getErrors(), ValidatorType.EMPTY_VALUE);


            Result<Phone, DomainError> nullNumber = Phone.create("+52", null);
            assertTrue(nullNumber.isFailure());
            assertErrorExists(nullNumber.getErrors(), ValidatorType.EMPTY_VALUE);

            Result<Phone, DomainError> bothNull = Phone.create(null, null);
            assertTrue(bothNull.isFailure());
            assertEquals(2, bothNull.getErrors().size());
            assertErrorExistsForField(bothNull.getErrors(), ValidatorType.EMPTY_VALUE, "phoneCountryCode");
            assertErrorExistsForField(bothNull.getErrors(), ValidatorType.EMPTY_VALUE, "number");
        }

        @Test
        @DisplayName("Should fail when country code or number is empty")
        void shouldFailWhenPhonePartsAreEmpty() {
            Result<Phone, DomainError> emptyCountry = Phone.create("", "5544332211");
            assertTrue(emptyCountry.isFailure());
            assertOnlyErrorExists(emptyCountry.getErrors(), ValidatorType.EMPTY_VALUE);

            Result<Phone, DomainError> emptyNumber = Phone.create("+52", "");
            assertTrue(emptyNumber.isFailure());
            assertOnlyErrorExists(emptyNumber.getErrors(), ValidatorType.EMPTY_VALUE);

            Result<Phone, DomainError> bothEmpty = Phone.create("", "");
            assertTrue(bothEmpty.isFailure());
            assertEquals(2, bothEmpty.getErrors().size());
            assertOnlyErrorExistsForField(bothEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "phoneCountryCode");
            assertOnlyErrorExistsForField(bothEmpty.getErrors(), ValidatorType.EMPTY_VALUE, "number");
        }

        @Test
        @DisplayName("Should fail when country code or number is whitespace")
        void shouldFailWhenPhonePartsAreWhitespace() {
            String whitespace = "   ";
            Result<Phone, DomainError> whiteCountry = Phone.create(whitespace, "5544332211");
            assertTrue(whiteCountry.isFailure());
            assertOnlyErrorExists(whiteCountry.getErrors(), ValidatorType.EMPTY_VALUE);

            Result<Phone, DomainError> whiteNumber = Phone.create("+52", whitespace);
            assertTrue(whiteNumber.isFailure());
            assertOnlyErrorExists(whiteNumber.getErrors(), ValidatorType.EMPTY_VALUE);

            Result<Phone, DomainError> bothWhite = Phone.create(whitespace, whitespace);
            assertTrue(bothWhite.isFailure());
            assertEquals(2, bothWhite.getErrors().size());
            assertOnlyErrorExistsForField(bothWhite.getErrors(), ValidatorType.EMPTY_VALUE, "phoneCountryCode");
            assertOnlyErrorExistsForField(bothWhite.getErrors(), ValidatorType.EMPTY_VALUE, "number");
        }

        @Test
        @DisplayName("Should fail when number format is invalid")
        void shouldFailWhenPhoneNumberIsInvalid() {
            // Invalid: letters, too short, too long, or non-digit characters
            String[] invalidNumbers = {"abcd", "123", "1a34", "12345678901234567890"};
            for (String invalid : invalidNumbers) {
                Result<Phone, DomainError> result = Phone.create("+52", invalid);
                assertTrue(result.isFailure(), "Expected failure for invalid number: " + invalid);
                assertOnlyErrorExists(result.getErrors(), ValidatorType.INVALID_FORMAT);
            }
        }

        @Test
        @DisplayName("Should fail when country code format is invalid")
        void shouldFailWhenCountryCodeIsInvalid() {
            // Invalid: missing '+', too long, contains letters
            String[] invalidCodes = {"52", "++123", "+abcd", "+12345"};
            for (String invalid : invalidCodes) {
                Result<Phone, DomainError> result = Phone.create(invalid, "5544332211");
                assertTrue(result.isFailure(), "Expected failure for invalid country code: " + invalid);
                assertOnlyErrorExists(result.getErrors(), ValidatorType.INVALID_FORMAT);
            }
        }
    }

    @Nested
    @DisplayName("Integration Cases")
    class IntegrationCases {

        static Stream<String> invalidPhonesProvider() throws IOException {
            return Files.lines(Paths.get("src", "test", "resources", "user", "invalid_phones.txt"));
        }

        @ParameterizedTest
        @MethodSource("invalidPhonesProvider")
        @DisplayName("Should fail to create Phone with invalid inputs from file")
        void shouldFailToCreatePhoneWithInvalidInputsFromFile(String fullPhone) {
            String[] parts = fullPhone.split(" ", 2);
            String countryCode = parts[0].equalsIgnoreCase("null") ? null : parts[0];
            String number = (parts.length > 1)
                    ? (parts[1].equalsIgnoreCase("null") ? null : parts[1])
                    : null;

            Result<Phone, DomainError> result = Phone.create(countryCode, number);
            assertTrue(result.isFailure(), "Expected failure for invalid phone: " + fullPhone);
        }

        @Test
        @DisplayName("Should get complete phone number")
        void shouldGetCompletePhoneNumber() {
            Phone phone = Phone.create("+52", "5544332211").getValue();
            String expectedCompleteNumber = "+52-5544332211";
            assertEquals(expectedCompleteNumber, phone.getValue());
        }

        @Test
        @DisplayName("Should be equal when having the same country code and number")
        void shouldBeEqualWhenHavingSameCountryCodeAndNumber() {
            Phone phone1 = Phone.create("+52", "5544332211").getValue();
            Phone phone2 = Phone.create("+52", "5544332211").getValue();
            assertEquals(phone1, phone2);
        }

        @Test
        @DisplayName("Should not be equal when having different country code or number")
        void shouldNotBeEqualWhenHavingDifferentCountryCodeOrNumber() {
            Phone phone1 = Phone.create("+52", "5544332211").getValue();
            Phone phone2 = Phone.create("+1", "5544332211").getValue();
            Phone phone3 = Phone.create("+52", "1234567890").getValue();
            assertNotEquals(phone1, phone2);
            assertNotEquals(phone1, phone3);
        }
    }
}