package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Role Value Object Test")
class RoleTest {

    // ---------------------------
    // Constantes del test
    // ---------------------------

    private static final String VALID_DEBTOR = "DEBTOR";
    private static final String VALID_CREDITOR = "CREDITOR";

    private static final String LOWERCASE_DEBTOR = "debtor";
    private static final String INVALID_ROLE = "OWNER";
    private static final String BLANK = "   ";
    private static final String EMPTY = "";


    // ---------------------------
    // Tests create()
    // ---------------------------

    @Test
    void create_ShouldReturnSuccess_WhenValidDebtorRole() {
        var result = Role.create(VALID_DEBTOR);

        assertTrue(result.isOk());
        assertEquals(VALID_DEBTOR, result.get().getValue());
    }

    @Test
    void create_ShouldReturnSuccess_WhenValidCreditorRole() {
        var result = Role.create(VALID_CREDITOR);

        assertTrue(result.isOk());
        assertEquals(VALID_CREDITOR, result.get().getValue());
    }

    @Test
    void create_ShouldBeCaseInsensitive() {
        var result = Role.create(LOWERCASE_DEBTOR);

        assertTrue(result.isOk());
        assertEquals(LOWERCASE_DEBTOR.toUpperCase(), result.get().getValue().toUpperCase());
    }

    @Test
    void create_ShouldFail_WhenRoleIsInvalid() {
        var result = Role.create(INVALID_ROLE);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenRoleIsBlank() {
        var result = Role.create(BLANK);

        assertTrue(result.isFailure());
    }

    @Test
    void create_ShouldFail_WhenRoleIsEmpty() {
        var result = Role.create(EMPTY);

        assertTrue(result.isFailure());
    }


    // ---------------------------
    // Tests load()
    // ---------------------------

    @Test
    void load_ShouldReturnRoleWithoutValidation() {
        Role role = Role.load("SOMETHING");

        assertEquals("SOMETHING", role.getValue());
    }


    // ---------------------------
    // Tests validateRol()
    // ---------------------------

    @Test
    void validateRol_ShouldReturnSuccess_WhenValid() {
        var result = Role.validateRol(VALID_DEBTOR, "role");

        assertTrue(result.isOk());
    }

    @Test
    void validateRol_ShouldReturnFailure_WhenInvalid() {
        var result = Role.validateRol(INVALID_ROLE, "role");

        assertTrue(result.isFailure());
    }

    @Test
    void validateRol_ShouldReturnFailure_WhenBlank() {
        var result = Role.validateRol(BLANK, "role");

        assertTrue(result.isFailure());
    }
}
