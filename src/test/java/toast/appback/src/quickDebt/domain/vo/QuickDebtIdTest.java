package toast.appback.src.quickDebt.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.quickDebt.domain.QuickDebtId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuickDebtId Value Object Test")
public class QuickDebtIdTest {

    // --- 1. Casos de Generación ---
    @Nested
    @DisplayName("Generation Cases (generateQuickDebtId())")
    class GenerationCases {

        @Test
        @DisplayName("Should generate a non-null ID")
        void shouldGenerateNonNullId() {
            QuickDebtId id = QuickDebtId.generateQuickDebtId();
            assertNotNull(id, "El ID generado no debe ser nulo.");
        }

        @Test
        @DisplayName("Should generate unique IDs for different calls")
        void shouldGenerateUniqueIds() {
            QuickDebtId id1 = QuickDebtId.generateQuickDebtId();
            QuickDebtId id2 = QuickDebtId.generateQuickDebtId();

            // Dos IDs generados aleatoriamente deberían ser diferentes.
            assertNotEquals(id1, id2, "Dos IDs generados deben ser distintos.");
        }
    }

    // --- 2. Casos de Carga ---
    @Nested
    @DisplayName("Loading Cases (load())")
    class LoadingCases {

        private final UUID fixedUuid = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");

        @Test
        @DisplayName("Should successfully load a valid UUID")
        void shouldLoadValidUuid() {
            QuickDebtId id = QuickDebtId.load(fixedUuid);

            assertNotNull(id, "El ID cargado no debe ser nulo.");
            // Verificación implícita de que el UUID interno se asignó correctamente
        }
    }

    // --- 3. Casos de Integración (Igualdad y Hash) ---
    @Nested
    @DisplayName("Integration Cases (Equality and HashCode)")
    class IntegrationCases {

        private final UUID SAME_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555");
        private final UUID DIFF_UUID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        @Test
        @DisplayName("Should be equal for QuickDebtIds encapsulating the same UUID")
        void shouldBeEqualForSameUuid() {
            QuickDebtId id1 = QuickDebtId.load(SAME_UUID);
            QuickDebtId id2 = QuickDebtId.load(SAME_UUID);

            // Prueba de igualdad VO: fundamental
            assertEquals(id1, id2, "Dos IDs que encapsulan el mismo UUID deben ser iguales.");
            assertEquals(id1, id2, "El método equals debe retornar true.");
        }

        @Test
        @DisplayName("Should not be equal for QuickDebtIds encapsulating different UUIDs")
        void shouldNotBeEqualForDifferentUuid() {
            QuickDebtId id1 = QuickDebtId.load(SAME_UUID);
            QuickDebtId id2 = QuickDebtId.load(DIFF_UUID);

            assertNotEquals(id1, id2, "Dos IDs que encapsulan diferentes UUIDs no deben ser iguales.");
        }

        @Test
        @DisplayName("Should have the same hash code for equal objects (Contract)")
        void shouldHaveSameHashCodeForEqualObjects() {
            QuickDebtId id1 = QuickDebtId.load(SAME_UUID);
            QuickDebtId id2 = QuickDebtId.load(SAME_UUID);

            // Contrato HashCode: Si a.equals(b), entonces a.hashCode() == b.hashCode()
            assertEquals(id1.hashCode(), id2.hashCode(), "Hash codes deben ser iguales para objetos iguales.");
        }

        @Test
        @DisplayName("Should return false when comparing with null or different class")
        void shouldHandleNullAndDifferentClass() {
            QuickDebtId id = QuickDebtId.load(SAME_UUID);

            // Comparación con nulo
            assertNotEquals(null, id, "Comparar con null debe ser false.");

            // Comparación con otra clase
            assertNotEquals(new Object(), id, "Comparar con una clase diferente debe ser false.");
        }
    }
}