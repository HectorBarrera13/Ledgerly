package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ajusta las importaciones de tu dominio
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.result.Result;

import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("Context Value Object Test")
class ContextTest {

    // ---------------------------
    // Constantes para el test
    // ---------------------------

    private static final int MAX_PURPOSE = 30;
    private static final int MAX_DESCRIPTION = 200;

    private static final String VALID_PURPOSE = "Compra de libros";
    private static final String VALID_DESCRIPTION =
            "Detalle de la transacción de compra de tres ejemplares.";

    private static final String BLANK = "   ";
    private static final String NULL_DESCRIPTION = null;

    // Genera strings largos para pruebas de límite
    private static final String TOO_LONG_PURPOSE = "A".repeat(MAX_PURPOSE + 1);
    private static final String TOO_LONG_DESCRIPTION = "A".repeat(MAX_DESCRIPTION + 1);


    // ---------------------------
    // Tests
    // ---------------------------

    @Test
    void create_ShouldReturnSuccess_WhenValidData() {
        var result = Context.create(VALID_PURPOSE, VALID_DESCRIPTION);

        assertTrue(result.isOk());
        assertEquals(VALID_PURPOSE, result.get().getPurpose());
        assertEquals(VALID_DESCRIPTION, result.get().getDescription());
    }

    @Test
    void create_ShouldFail_WhenPurposeIsNull() {
        var result = Context.create(null, VALID_DESCRIPTION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenPurposeIsBlank() {
        var result = Context.create(BLANK, VALID_DESCRIPTION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenPurposeExceedsMaxLength() {
        var result = Context.create(TOO_LONG_PURPOSE, VALID_DESCRIPTION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenDescriptionExceedsMaxLength() {
        var result = Context.create(VALID_PURPOSE, TOO_LONG_DESCRIPTION);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldAllowNullOrBlankDescription_WhenWithinLimits() {
        var resultNull = Context.create(VALID_PURPOSE, NULL_DESCRIPTION);
        var resultBlank = Context.create(VALID_PURPOSE, BLANK);

        assertTrue(resultNull.isOk());
        assertTrue(resultBlank.isOk());
    }

    @Test
    void load_ShouldReturnContext_WhenValid() {
        Context ctx = Context.load(VALID_PURPOSE, VALID_DESCRIPTION);

        assertEquals(VALID_PURPOSE, ctx.getPurpose());
        assertEquals(VALID_DESCRIPTION, ctx.getDescription());
    }

    @Test
    void load_ShouldThrowException_WhenInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> Context.load(TOO_LONG_PURPOSE, VALID_DESCRIPTION)
        );
    }
}
