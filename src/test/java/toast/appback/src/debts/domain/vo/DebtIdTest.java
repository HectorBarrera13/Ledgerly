package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebtId Value Object Test")
class DebtIdTest {

    // ---------------------------
    // Constantes del test
    // ---------------------------

    private static final UUID RAW_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID OTHER_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");


    // ---------------------------
    // Tests
    // ---------------------------

    @Test
    void generate_ShouldCreateDifferentIds() {
        DebtId id1 = DebtId.generate();
        DebtId id2 = DebtId.generate();

        assertNotEquals(id1, id2);
        assertNotEquals(id1.getValue(), id2.getValue());
    }

    @Test
    void load_ShouldReturnDebtIdWithSameUUID() {
        DebtId id = DebtId.load(RAW_UUID);

        assertEquals(RAW_UUID, id.getValue());
    }

    @Test
    void equals_ShouldReturnTrue_WhenSameUUID() {
        DebtId id1 = DebtId.load(RAW_UUID);
        DebtId id2 = DebtId.load(RAW_UUID);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentUUID() {
        DebtId id1 = DebtId.load(RAW_UUID);
        DebtId id2 = DebtId.load(OTHER_UUID);

        assertNotEquals(id1, id2);
    }

    @Test
    void equals_ShouldReturnFalse_WhenObjectIsNull() {
        DebtId id = DebtId.load(RAW_UUID);

        assertNotEquals(id, null);
    }

    @Test
    void equals_ShouldReturnFalse_WhenClassIsDifferent() {
        DebtId id = DebtId.load(RAW_UUID);

        assertNotEquals(id, RAW_UUID); // comparando contra otro tipo
    }

    @Test
    void getValue_ShouldReturnUUID() {
        DebtId id = DebtId.load(RAW_UUID);

        assertEquals(RAW_UUID, id.getValue());
    }
 }
