package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static toast.appback.src.shared.ValueObjectsUtils.assertErrorExists;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.domain.Validators; // Asumimos que esta clase existe
import toast.appback.src.shared.utils.result.Result; // Asumimos que esta clase existe

import java.util.List;

public class TargetUserTest {

    // ----------------------------------------
    // Tests para el método estático 'create'
    // ----------------------------------------

    @Test
    void testCreate_Success_ValidName() {
        String validName = "Juan Perez";

        Result<TargetUser, DomainError> result = TargetUser.create(validName);

        assertTrue(result.isOk(), "La creación debe ser exitosa para un nombre válido.");
        assertNotNull(result.get(), "El objeto TargetUser no debe ser null.");
        assertEquals(validName, result.get().getName(), "El nombre del TargetUser debe coincidir.");
    }

    @Test
    void testCreate_Failure_NullName() {
        String invalidName = null;

        Result<TargetUser, DomainError> result = TargetUser.create(invalidName);

        assertTrue(result.isFailure(), "La creación debe fallar si el nombre es null.");
        List<DomainError> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertErrorExists(errors, ValidatorType.EMPTY_VALUE);
    }

    @Test
    void testCreate_Failure_EmptyName() {
        // ARRANGE
        String invalidName = "";

        // ACT
        Result<TargetUser, DomainError> result = TargetUser.create(invalidName);

        // ASSERT
        assertTrue(result.isFailure(), "La creación debe fallar si el nombre está vacío.");
        List<DomainError> errors = result.getErrors();

        assertEquals(1, errors.size());
        assertErrorExists(errors, ValidatorType.EMPTY_VALUE);
    }

    @Test
    void testCreate_Failure_TooLongName() {
        // ARRANGE
        // 31 caracteres (mayor a MAX_NAME_LENGTH = 30)
        String longName = "a".repeat(31);

        // ACT
        Result<TargetUser, DomainError> result = TargetUser.create(longName);

        // ASSERT
        assertTrue(result.isFailure(), "La creación debe fallar si el nombre excede 30 caracteres.");
        List<DomainError> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertErrorExists(errors, ValidatorType.TOO_LONG);
    }

}