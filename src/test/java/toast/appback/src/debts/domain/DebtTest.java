package toast.appback.src.debts.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User; // Asumo que User tiene un método load()
import toast.appback.src.users.domain.UserId;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Debt Aggregate Test")
public class DebtTest {

    // Simulación de VOs y Entidades para facilitar la construcción
    private DebtId debtId;
    private Context context;
    private DebtMoney debtMoney;
    private User debtor;
    private User creditor;

    public class testDomainEvent implements DomainEvent{

    }

    @BeforeEach
    void setUp() {
        // Asume que los VOs y Entidades tienen un método load() o son fácilmente instanciables
        debtId = DebtId.load(UUID.randomUUID());
        context = Context.load("Test Purpose", "Test Description");
        debtMoney = DebtMoney.load(1000L, "USD");

        UserId debtorId = UserId.load(UUID.randomUUID());
        // Asume que Name tiene un método load() o usa el constructor/create para Name
        Name debtorName = Name.load("John", "Doe");
        // Asume que Phone tiene un método load()
        Phone debtorPhone = Phone.load("+52","123456789");

        // --- User VOs para CREDITOR ---
        UserId creditorId = UserId.load(UUID.randomUUID());
        Name creditorName = Name.load("Jane", "Smith");
        Phone creditorPhone = Phone.load("+52","987654321");

        // Asume User.load() existe
        debtor = new User(debtorId, debtorName, debtorPhone);
        creditor = new User(creditorId, creditorName, creditorPhone);
    }

    // Helper para crear una instancia base
    private Debt createPendingDebt() {
        return new Debt(debtId, context, debtMoney, debtor, creditor);
    }

    // Helper para verificar el código de error
    private void assertBusinessRuleError(Result<?, DomainError> result) {
        assertTrue(result.isFailure(), "El resultado debe ser un fallo.");
        assertEquals("BUSINESS_RULE", result.getErrors(), "El error debe ser de regla de negocio.");
    }

    // --- 1. Tests de Construcción ---

    @Nested
    @DisplayName("Constructor and Getters")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize Debt correctly using primary constructor")
        void shouldInitializeDebtCorrectly() {
            Debt debt = createPendingDebt();

            assertEquals(debtId, debt.getId());
            assertEquals(context, debt.getContext());
            assertEquals(debtMoney, debt.getAmount());
            assertEquals(debtor, debt.getDebtor());
            assertEquals(creditor, debt.getCreditor());
            // El estado inicial debe ser PENDING por defecto
            assertEquals(Status.PENDING, debt.getStatus());
            // La lista de eventos debe estar vacía
            assertNotNull(debt.getDebtEvents());
            assertTrue(debt.getDebtEvents().isEmpty());
        }

        @Test
        @DisplayName("Should initialize Debt correctly with existing events")
        void shouldInitializeDebtWithEvents() {
            List<DomainEvent> events = List.of(new testDomainEvent());

            Debt debt = new Debt(debtId, context, debtMoney, debtor, creditor, events);

            assertEquals(events, debt.getDebtEvents());
            assertEquals(1, debt.getDebtEvents().size());
        }
    }

    // --- 2. Tests de Comportamiento (Transición de Estado) ---

    @Nested
    @DisplayName("State Transition Methods")
    class StateTransitionTests {

        @Test
        @DisplayName("PENDING -> ACCEPTED: Should successfully accept the debt")
        void shouldAcceptDebtSuccessfully() {
            Debt debt = createPendingDebt();

            Result<Void, DomainError> result = debt.accept();

            assertTrue(result.isSuccess());
            assertEquals(Status.ACCEPTED, debt.getStatus(), "El estado debe cambiar a ACCEPTED.");
        }

        @Test
        @DisplayName("PENDING -> REJECTED: Should successfully reject the debt")
        void shouldRejectDebtSuccessfully() {
            Debt debt = createPendingDebt();

            Result<Void, DomainError> result = debt.reject();

            assertTrue(result.isSuccess());
            assertEquals(Status.REJECTED, debt.getStatus(), "El estado debe cambiar a REJECTED.");
        }

        @Test
        @DisplayName("ACCEPTED -> PAID: Should successfully pay the debt")
        void shouldPayDebtSuccessfully() {
            Debt debt = createPendingDebt();
            debt.accept(); // Transiciona a ACCEPTED

            Result<Void, DomainError> result = debt.pay();

            assertTrue(result.isSuccess());
            assertEquals(Status.PAID, debt.getStatus(), "El estado debe cambiar a PAID.");
        }

        // --- Reglas de Negocio (Fallos de Transición) ---

        @Test
        @DisplayName("REJECTED -> ACCEPTED: Should fail to accept if status is REJECTED")
        void shouldFailToAcceptIfRejected() {
            Debt debt = createPendingDebt();
            debt.reject(); // Estado: REJECTED

            Result<Void, DomainError> result = debt.accept();

            assertBusinessRuleError(result);
            assertEquals(Status.REJECTED, debt.getStatus(), "El estado no debe cambiar.");
        }

        @Test
        @DisplayName("PAID -> REJECTED: Should fail to reject if status is PAID")
        void shouldFailToRejectIfPaid() {
            Debt debt = createPendingDebt();
            debt.accept();
            debt.pay(); // Estado: PAID

            Result<Void, DomainError> result = debt.reject();

            assertBusinessRuleError(result);
            assertEquals(Status.PAID, debt.getStatus(), "El estado no debe cambiar.");
        }

        @Test
        @DisplayName("PENDING -> PAID: Should fail to pay if status is PENDING")
        void shouldFailToPayIfPending() {
            Debt debt = createPendingDebt(); // Estado: PENDING

            Result<Void, DomainError> result = debt.pay();

            // La lógica actual de pay() falla si no es ACCEPTED.
            assertBusinessRuleError(result);
            assertEquals(Status.PENDING, debt.getStatus(), "El estado no debe cambiar.");
        }
    }

    // --- 3. Tests de Comportamiento (Edición) ---

    @Nested
    @DisplayName("Modification Methods (editAmount)")
    class ModificationTests {

        @Test
        @DisplayName("Should successfully edit amount if status is PENDING")
        void shouldEditAmountIfPending() {
            Debt debt = createPendingDebt();

            // Nuevo monto: 50.00 USD (5000L sin escalar)
            DebtMoney newAmount = DebtMoney.load(5000L, "USD");

            Result<Void, DomainError> result = debt.editAmount(newAmount);

            assertTrue(result.isSuccess());
            assertEquals(newAmount, debt.getAmount(), "El monto debe ser actualizado.");
            assertEquals(Status.PENDING, debt.getStatus(), "El estado debe permanecer PENDING.");
        }

        @Test
        @DisplayName("Should fail to edit amount if status is ACCEPTED")
        void shouldFailToEditAmountIfAccepted() {
            Debt debt = createPendingDebt();
            debt.accept(); // Estado: ACCEPTED

            DebtMoney newAmount = DebtMoney.load(5000L, "USD");

            Result<Void, DomainError> result = debt.editAmount(newAmount);

            assertBusinessRuleError(result);
            // Verifica que el monto NO haya cambiado
            assertEquals(debtMoney, debt.getAmount(), "El monto no debe ser actualizado.");
        }

        @Test
        @DisplayName("Should fail to edit amount if status is PAID")
        void shouldFailToEditAmountIfPaid() {
            Debt debt = createPendingDebt();
            debt.accept();
            debt.pay(); // Estado: PAID

            DebtMoney newAmount = DebtMoney.load(5000L, "USD");

            Result<Void, DomainError> result = debt.editAmount(newAmount);

            assertBusinessRuleError(result);
            // Verifica que el monto NO haya cambiado
            assertEquals(debtMoney, debt.getAmount(), "El monto no debe ser actualizado.");
        }
    }
}
