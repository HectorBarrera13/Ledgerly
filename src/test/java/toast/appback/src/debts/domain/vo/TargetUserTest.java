package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static toast.appback.src.shared.ValueObjectsUtils.assertErrorExists;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.domain.Validators; // Asumimos que esta clase existe
import toast.appback.src.shared.utils.result.Result; // Asumimos que esta clase existe

import java.util.List;
class TargetUserTest {

    // ---------------------------
    // Constantes del test
    // ---------------------------

    private static final String VALID_NAME = "Juan Perez";
    private static final String EMPTY = "";
    private static final String NULL_NAME = null;
    private static final String LONG_NAME = "A".repeat(31); // más de 30 caracteres


    // ---------------------------
    // Tests create()
    // ---------------------------

    @Test
    void create_ShouldReturnSuccess_WhenValidName() {
        var result = TargetUser.create(VALID_NAME);

        assertTrue(result.isOk());
        assertEquals(VALID_NAME, result.get().getName());
    }

    @Test
    void create_ShouldFail_WhenNameIsNull() {
        var result = TargetUser.create(NULL_NAME);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenNameIsEmpty() {
        var result = TargetUser.create(EMPTY);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenNameExceedsMaxLength() {
        var result = TargetUser.create(LONG_NAME);

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // Tests load()
    // ---------------------------

    @Test
    void load_ShouldReturnTargetUserWithoutValidation() {
        TargetUser tu = TargetUser.load("alguien");

        assertEquals("alguien", tu.getName());
    }


    // ---------------------------
    // Tests nameValidation()
    // ---------------------------

    @Test
    void nameValidation_ShouldReturnSuccess_WhenValid() {
        var result = TargetUser.nameValidation(VALID_NAME, "name");

        assertTrue(result.isOk());
    }

    @Test
    void nameValidation_ShouldFail_WhenNull() {
        var result = TargetUser.nameValidation(NULL_NAME, "name");

        assertTrue(result.isFailure());
    }

    @Test
    void nameValidation_ShouldFail_WhenEmpty() {
        var result = TargetUser.nameValidation(EMPTY, "name");

        assertTrue(result.isFailure());
    }

    @Test
    void nameValidation_ShouldFail_WhenTooLong() {
        var result = TargetUser.nameValidation(LONG_NAME, "name");

        assertTrue(result.isFailure());
    }
}