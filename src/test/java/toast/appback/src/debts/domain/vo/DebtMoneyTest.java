package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ajusta las importaciones de tu dominio
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.result.Result;

import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("DebtMoney Value Object Test")
class
DebtMoneyTest {

    // ---------------------------
    // Constantes del test
    // ---------------------------

    private static final String VALID_CURRENCY = "MXN";
    private static final String INVALID_CURRENCY = "MEXX";
    private static final String NULL_CURRENCY = null;

    private static final Long VALID_AMOUNT = 1500L; // representa 15.00
    private static final Long NEGATIVE_AMOUNT = -10L;
    private static final Long NULL_AMOUNT = null;

    private static final BigDecimal EXPECTED_TRANSFORMED_AMOUNT =
            new BigDecimal("15.00"); // 1500 con escala 2

    // ---------------------------
    // Tests
    // ---------------------------

    @Test
    void create_ShouldReturnSuccess_WhenValidData() {
        var result = DebtMoney.create(VALID_CURRENCY, VALID_AMOUNT);

        assertTrue(result.isOk());
        assertEquals(VALID_CURRENCY, result.get().getCurrency());
        assertEquals(EXPECTED_TRANSFORMED_AMOUNT, result.get().getAmount());
    }

    @Test
    void create_ShouldFail_WhenCurrencyIsNull() {
        var result = DebtMoney.create(NULL_CURRENCY, VALID_AMOUNT);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenCurrencyFormatIsInvalid() {
        var result = DebtMoney.create(INVALID_CURRENCY, VALID_AMOUNT);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenAmountIsNull() {
        var result = DebtMoney.create(VALID_CURRENCY, NULL_AMOUNT);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenAmountIsNegative() {
        var result = DebtMoney.create(VALID_CURRENCY, NEGATIVE_AMOUNT);

        assertTrue(result.isFailure());
    }

    @Test
    void load_ShouldCreateObjectWithoutValidation() {
        BigDecimal amount = new BigDecimal("99.99");
        String currency = "USD";

        DebtMoney money = DebtMoney.load(amount, currency);

        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentAmount() {
        var m1 = DebtMoney.create(VALID_CURRENCY, 1000L).get();
        var m2 = DebtMoney.create(VALID_CURRENCY, 2000L).get();

        assertNotEquals(m1, m2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentCurrency() {
        var m1 = DebtMoney.create("USD", VALID_AMOUNT).get();
        var m2 = DebtMoney.create("EUR", VALID_AMOUNT).get();

        assertNotEquals(m1, m2);
    }

    @Test
    void amountTransformation_ShouldScaleCorrectly() {
        var result = DebtMoney.create(VALID_CURRENCY, VALID_AMOUNT).get();

        assertEquals(EXPECTED_TRANSFORMED_AMOUNT, result.getAmount());
    }
}