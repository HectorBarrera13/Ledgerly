package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ajusta las importaciones de tu dominio
import toast.appback.src.debts.domain.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.Result;

import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("DebtMoney Value Object Test")
public class DebtMoneyTest {
    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_AMOUNT = "amount";

    private static final int SCALE = 2; // Escala esperada por el VO
    private static final String VALID_CURRENCY = "USD";
    private static final Long VALID_AMOUNT_LONG = 12345L; // Representa 123.45
    private static final String DUMMY_UNIT_SCALE = "CENTS"; // Parámetro ignorado en la lógica actual

    // --- 1. Casos Válidos ---
    // Simula tu clase 'ValidCases'
    @Nested
    @DisplayName("Valid Cases (Creation)")
    class ValidCases {

        @Test
        @DisplayName("Should create DebtMoney successfully with valid inputs")
        void shouldCreateDebtMoneySuccessfullyWithValidInputs() {
            Result<DebtMoney, DomainError> result = DebtMoney.create(VALID_CURRENCY, VALID_AMOUNT_LONG);

            assertTrue(result.isSuccess(), "Expected success for valid currency and amount.");

            DebtMoney money = result.getValue();
            assertEquals(VALID_CURRENCY, money.getCurrency());

            // Verifica la transformación a BigDecimal
            BigDecimal expectedAmount = new BigDecimal("123.45").setScale(SCALE, RoundingMode.UNNECESSARY);
            assertEquals(expectedAmount, money.getAmount());
            assertEquals(SCALE, money.getAmount().scale());
        }
    }

    // --- 2. Casos Inválidos ---
    // Simula tu clase 'InvalidCases'
    @Nested
    @DisplayName("Invalid Cases (Validation Failures)")
    class InvalidCases {

        @Test
        @DisplayName("Should fail when amount is negative (MUST_BE_POSITIVE)")
        void shouldFailWhenAmountIsNegative() {
            Long negativeAmount = -1L;

            Result<DebtMoney, DomainError> result = DebtMoney.create(VALID_CURRENCY, negativeAmount );

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.MUST_BE_POSITIVE);
        }

        @Test
        @DisplayName("Should fail when currency format is invalid (INVALID_FORMAT)")
        void shouldFailWhenCurrencyFormatIsInvalid() {
            String invalidCurrency = "usd"; // Debería ser mayúsculas

            Result<DebtMoney, DomainError> result = DebtMoney.create(invalidCurrency, VALID_AMOUNT_LONG );

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.INVALID_FORMAT);
        }

        @Test
        @DisplayName("Should fail when currency is null (EMPTY_VALUE)")
        void shouldFailWhenCurrencyIsNull() {
            Result<DebtMoney, DomainError> result = DebtMoney.create(null, VALID_AMOUNT_LONG );

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors,  ValidatorType.EMPTY_VALUE);
        }

        @Test
        @DisplayName("Should fail when amount is null (EMPTY_VALUE)")
        void shouldFailWhenAmountIsNull() {
            Result<DebtMoney, DomainError> result = DebtMoney.create(VALID_CURRENCY, null );

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.EMPTY_VALUE);
        }

        @Test
        @DisplayName("Should fail and accumulate errors when both fields are invalid")
        void shouldFailAndAccumulateErrors() {
            String invalidCurrency = "U"; // Formato inválido
            Long negativeAmount = -5L;    // Negativo

            // Result.combine() debe acumular ambos errores
            Result<DebtMoney, DomainError> result = DebtMoney.create(invalidCurrency, negativeAmount );

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(2, errors.size());

            // Verifica que ambos errores estén presentes
            assertErrorExistsForField(errors, ValidatorType.INVALID_FORMAT, FIELD_CURRENCY);
            assertErrorExistsForField(errors, ValidatorType.MUST_BE_POSITIVE, FIELD_AMOUNT);
        }
    }

    // --- 3. Casos de Integración (Propiedades y Comparación) ---
    // Simula tu clase 'IntegrationCases'
    @Nested
    @DisplayName("Integration Cases (Equality and Accessors)")
    class IntegrationCases {

        @Test
        @DisplayName("Should be equal for same currency and amount")
        void shouldBeEqualForSameCurrencyAndAmount() {
            Result<DebtMoney, DomainError> result1 = DebtMoney.create("EUR", 500L );
            Result<DebtMoney, DomainError> result2 = DebtMoney.create("EUR", 500L );

            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());

            assertEquals(result1.getValue(), result2.getValue(), "Dos objetos DebtMoney con los mismos valores deben ser iguales.");
            assertEquals(result1.getValue().hashCode(), result2.getValue().hashCode(), "Hash codes deben coincidir.");
        }

        @Test
        @DisplayName("Should not be equal for different currency or amount")
        void shouldNotBeEqualForDifferentValues() {
            Result<DebtMoney, DomainError> result1 = DebtMoney.create("EUR", 500L); // 5.00 EUR
            Result<DebtMoney, DomainError> result2 = DebtMoney.create("USD", 500L ); // 5.00 USD
            Result<DebtMoney, DomainError> result3 = DebtMoney.create("EUR", 600L ); // 6.00 EUR

            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());
            assertTrue(result3.isSuccess());

            assertNotEquals(result1.getValue(), result2.getValue(), "Diferente divisa.");
            assertNotEquals(result1.getValue(), result3.getValue(), "Diferente monto.");
        }

        @Test
        @DisplayName("Should have correct getters")
        void shouldHaveCorrectGetters() {
            Result<DebtMoney, DomainError> result = DebtMoney.create("CAD", 123L); // 1.23 CAD
            assertTrue(result.isSuccess());
            DebtMoney money = result.getValue();

            assertEquals("CAD", money.getCurrency());
            assertEquals(new BigDecimal("1.23").setScale(SCALE, RoundingMode.UNNECESSARY), money.getAmount());
        }
    }
}