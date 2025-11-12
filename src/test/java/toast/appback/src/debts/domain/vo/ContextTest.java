package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ajusta las importaciones de tu dominio
import toast.appback.src.debts.domain.Context;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.Result;

import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("Context Value Object Test")
public class ContextTest {

    private static final int MAX_PURPOSE = 30;
    private static final int MAX_DESCRIPTION = 200;
    private static final String VALID_PURPOSE = "Compra de libros";
    private static final String VALID_DESCRIPTION = "Detalle de la transacción de compra de tres ejemplares.";

    // --- 1. Casos Válidos ---
    @Nested
    @DisplayName("Valid Cases (Creation)")
    class ValidCases {

        @Test
        @DisplayName("Should create Context successfully with valid purpose and description")
        void shouldCreateContextSuccessfully() {
            Result<Context, DomainError> result = Context.create(VALID_PURPOSE, VALID_DESCRIPTION);

            assertTrue(result.isSuccess(), "Expected success for valid inputs.");

            Context context = result.getValue();
            assertEquals(VALID_PURPOSE, context.getPurpose());
            assertEquals(VALID_DESCRIPTION, context.getDescription());
        }

        @Test
        @DisplayName("Should create Context successfully when description is empty (valid)")
        void shouldCreateContextWithEmptyDescription() {
            String emptyDescription = "";
            Result<Context, DomainError> result = Context.create(VALID_PURPOSE, emptyDescription);

            assertTrue(result.isSuccess(), "Description can be empty.");

            Context context = result.getValue();
            assertEquals(VALID_PURPOSE, context.getPurpose());
            assertEquals(emptyDescription, context.getDescription());
        }

        @Test
        @DisplayName("Should create Context successfully when description is only whitespace (valid)")
        void shouldCreateContextWithWhitespaceDescription() {
            String whitespaceDescription = "  ";
            Result<Context, DomainError> result = Context.create(VALID_PURPOSE, whitespaceDescription);

            assertTrue(result.isSuccess(), "Description can contain only whitespace.");
            assertEquals(whitespaceDescription, result.getValue().getDescription());
        }
    }

    // --- 2. Casos Inválidos ---
    @Nested
    @DisplayName("Invalid Cases (Validation Failures)")
    class InvalidCases {

        // --- Fallos en Purpose ---

        @Test
        @DisplayName("Should fail when purpose is blank/empty (EMPTY_VALUE)")
        void shouldFailWhenPurposeIsBlank() {
            String blankPurpose = "   "; // O ""
            Result<Context, DomainError> result = Context.create(blankPurpose, VALID_DESCRIPTION);

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.EMPTY_VALUE);
        }

        @Test
        @DisplayName("Should fail when purpose is too long (TOO_LONG)")
        void shouldFailWhenPurposeIsTooLong() {
            String longPurpose = "A".repeat(MAX_PURPOSE + 1);

            Result<Context, DomainError> result = Context.create(longPurpose, VALID_DESCRIPTION);

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.TOO_LONG);
        }

        // --- Fallos en Description ---

        @Test
        @DisplayName("Should fail when description is too long (TOO_LONG)")
        void shouldFailWhenDescriptionIsTooLong() {
            String longDescription = "B".repeat(MAX_DESCRIPTION + 1);

            Result<Context, DomainError> result = Context.create(VALID_PURPOSE, longDescription);

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(1, errors.size());
            assertErrorExists(errors, ValidatorType.TOO_LONG);
        }

        // --- Fallo Combinado ---

        @Test
        @DisplayName("Should fail and accumulate errors when both fields are invalid")
        void shouldFailAndAccumulateErrors() {
            String longPurpose = "C".repeat(MAX_PURPOSE + 1);
            String longDescription = "D".repeat(MAX_DESCRIPTION + 1);

            // Result.combine() debe acumular ambos errores
            Result<Context, DomainError> result = Context.create(longPurpose, longDescription);

            assertTrue(result.isFailure());
            List<DomainError> errors = result.getErrors();
            assertEquals(2, errors.size(), "Debe retornar dos errores.");

            // Verificar ambos errores
            assertErrorExistsForField(errors, ValidatorType.TOO_LONG, "purpose");
            assertErrorExistsForField(errors, ValidatorType.TOO_LONG, "description");
        }
    }

    // --- 3. Casos de Integración ---
    @Nested
    @DisplayName("Integration Cases (Equality and Accessors)")
    class IntegrationCases {

        @Test
        @DisplayName("Should be equal for same purpose and description")
        void shouldBeEqualForSameValues() {
            Result<Context, DomainError> result1 = Context.create("Viaje", "Para el boleto.");
            Result<Context, DomainError> result2 = Context.create("Viaje", "Para el boleto.");

            assertTrue(result1.isSuccess());
            assertNotEquals(result1.getValue(), result2.getValue(), "Dos objetos Context con los mismos valores deben ser iguales.");
            assertNotEquals(result1.getValue().hashCode(), result2.getValue().hashCode(), "Hash codes deben coincidir.");
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            Result<Context, DomainError> result1 = Context.create("Cena", "Hoy.");
            Result<Context, DomainError> result2 = Context.create("Cena", "Ayer.");

            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());

            assertNotEquals(result1.getValue(), result2.getValue(), "Diferente descripción.");
        }

        @Test
        @DisplayName("Should return the correct values using load() and accessors")
        void shouldReturnCorrectValuesUsingLoad() {
            String purpose = "Pago";
            String description = "Factura de servicios.";

            Context context = Context.load(purpose, description);

            assertEquals(purpose, context.getPurpose());
            assertEquals(description, context.getDescription());
        }
    }
}
