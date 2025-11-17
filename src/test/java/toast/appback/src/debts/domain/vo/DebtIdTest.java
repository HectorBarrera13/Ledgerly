package toast.appback.src.debts.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebtId Value Object Test")
public class DebtIdTest {

    // --- 1. Casos de Generación ---
    @Nested
    @DisplayName("Generation Cases (generate())")
    class GenerationCases {

        @Test
        @DisplayName("Should generate a non-null ID")
        void shouldGenerateNonNullId() {
            DebtId id = DebtId.generate();
            assertNotNull(id, "El ID generado no debe ser nulo.");
        }

        @Test
        @DisplayName("Should generate unique IDs for different calls")
        void shouldGenerateUniqueIds() {
            DebtId id1 = DebtId.generate();
            DebtId id2 = DebtId.generate();

            // Un UUID aleatorio tiene una probabilidad extremadamente baja de ser igual.
            assertNotEquals(id1, id2, "Dos llamadas a generate() deben producir IDs diferentes.");
        }
    }

    // --- 2. Casos de Carga ---
    @Nested
    @DisplayName("Loading Cases (load())")
    class LoadingCases {

        @Test
        @DisplayName("Should successfully load a valid UUID")
        void shouldLoadValidUuid() {
            UUID testUuid = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
            DebtId id = DebtId.load(testUuid);

            assertNotNull(id, "El ID cargado no debe ser nulo.");
        }
    }

    // --- 3. Casos de Integración (Igualdad y Hash) ---
    @Nested
    @DisplayName("Integration Cases (Equality and HashCode)")
    class IntegrationCases {

        private final UUID SAME_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555");
        private final UUID DIFF_UUID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        @Test
        @DisplayName("Should be equal for DebtIds encapsulating the same UUID")
        void shouldBeEqualForSameUuid() {
            DebtId id1 = DebtId.load(SAME_UUID);
            DebtId id2 = DebtId.load(SAME_UUID);

            // La igualdad de VO es el test más importante.
            assertEquals(id1, id2, "Dos IDs que encapsulan el mismo UUID deben ser iguales.");
        }

        @Test
        @DisplayName("Should not be equal for DebtIds encapsulating different UUIDs")
        void shouldNotBeEqualForDifferentUuid() {
            DebtId id1 = DebtId.load(SAME_UUID);
            DebtId id2 = DebtId.load(DIFF_UUID);

            assertNotEquals(id1, id2, "Dos IDs que encapsulan diferentes UUIDs no deben ser iguales.");
        }

        @Test
        @DisplayName("Should have the same hash code for equal objects")
        void shouldHaveSameHashCodeForEqualObjects() {
            DebtId id1 = DebtId.load(SAME_UUID);
            DebtId id2 = DebtId.load(SAME_UUID);

            assertEquals(id1.hashCode(), id2.hashCode(), "Hash codes deben ser iguales para objetos iguales.");
        }
    }
}
