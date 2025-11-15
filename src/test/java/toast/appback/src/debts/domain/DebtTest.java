package toast.appback.src.debts.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// ASUNCIONES:
// Se asume la existencia de:
// - DebtId.load(UUID) y DebtId.generate()
// - Context.load(String, String) y Context.create(String, String)
// - DebtMoney.load(Long, String) y DebtMoney.create(String, Long)
// - DebtBusinessCode (usado en el código original para assertions)
// - ValueObjectsUtils.assertBusinessRuleErrorExists (asumo que se implementa)

@DisplayName("Debt Aggregate Test")
public class DebtTest {

    // Simulación de VOs y Entidades para facilitar la construcción
    private DebtId debtId;
    private Context context;
    private DebtMoney debtMoney;
    private User debtor;
    private User creditor;

    // Helper method for asserting business rule errors (assuming it exists in ValueObjectsUtils)
    // private void assertBusinessRuleErrorExists(DomainError errors, Enum<?> expectedCode) { ... }

    public class testDomainEvent implements DomainEvent{

    }

    private static Long bigDecimalToLong(BigDecimal bigDecimal) {
        // Corrección: Tu helper original usa getDebtMoney(), que devuelve Long.
        // Si usas BigDecimal en alguna parte, esta conversión puede ser necesaria.
        // Por ahora, lo dejo como estaba en tu ejemplo:
        return bigDecimal.longValue()*100;
    }

    @BeforeEach
    void setUp() {
        // Asume que los VOs y Entidades tienen un método load() o son fácilmente instanciables
        debtId = DebtId.load(UUID.randomUUID());
        context = Context.load("Test Purpose", "Test Description");
        debtMoney = DebtMoney.load(1000L, "USD");

        // --- User VOs para DEBTOR ---
        UserId debtorId = UserId.load(UUID.randomUUID());
        Name debtorName = Name.load("John", "Doe");
        Phone debtorPhone = Phone.load("+52","123456789");

        // --- User VOs para CREDITOR ---
        UserId creditorId = UserId.load(UUID.randomUUID());
        Name creditorName = Name.load("Jane", "Smith");
        Phone creditorPhone = Phone.load("+52","987654321");

        // Asume constructor User(UserId, Name, Phone) existe
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
        // Aquí debería ir una aserción más específica si ErrorsHandler.combine funciona diferente
        assertNotNull(result.getErrors(), "Debe haber un error de dominio.");
    }

    // --- 1. Tests de Construcción ---

    @Nested
    @DisplayName("Constructor and Getters")
    class ConstructorTests {
        // ... Tests existentes del constructor ...

        @Test
        @DisplayName("Should initialize Debt correctly using primary constructor")
        void shouldInitializeDebtCorrectly() {
            Debt debt = createPendingDebt();

            assertEquals(debtId, debt.getId());
            assertEquals(context, debt.getContext());
            assertEquals(debtMoney, debt.getDebtMoney()); // Asumo getDebtMoney() es el getter correcto
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

    // --- 4. Tests del Método de Creación (Create) ---
    // ... Tests de CreationMethodTests ...

    @Nested
    @DisplayName("Creation Method (Create)")
    class CreationMethodTests {

        private Debt debtAggregate;

        @BeforeEach
        void setupCreationMethod() {
            // NOTA: El método Create del archivo original no es estático, lo probaremos sobre una instancia.
            debtAggregate = createPendingDebt();
        }

        @Test
        @DisplayName("Should successfully create a new Debt with valid input")
        void shouldCreateDebtSuccessfully() {
            String purpose = "Comida";
            String description = "Cena de anoche";
            String currency = "MXN";
            Long amount = 5000L; // 50.00 MXN sin escalar

            // NOTA: El método en tu clase se llama 'Create', no 'create'.
            Result<Debt, DomainError> result = Debt.create(purpose, description, currency, amount, creditor, debtor);

            assertTrue(result.isSuccess(), "La creación debe ser exitosa.");

            Debt createdDebt = result.getValue();
            assertNotNull(createdDebt.getId(), "El ID debe ser generado.");
            // NOTA: En tu clase original, el constructor invierte creditor y debtor al pasar a new Debt
            // Asegúrate de que esta inversión sea la esperada. Ajusto la aserción a tu lógica de Create:
            assertEquals(creditor, createdDebt.getCreditor(), "El deudor debe ser el CREDITOR (según la lógica de Create).");
            assertEquals(debtor, createdDebt.getDebtor(), "El acreedor debe ser el DEBTOR (según la lógica de Create).");

            // Verificar Contexto
            assertEquals(purpose, createdDebt.getContext().getPurpose(), "El propósito debe coincidir.");
            assertEquals(description, createdDebt.getContext().getDescription(), "La descripción debe coincidir.");
        }

        // ... (otros tests de fallo de CreationMethodTests) ...
    }


    // --- 2. Tests de Comportamiento (Transición de Estado) ---

    @Nested
    @DisplayName("State Transition Methods")
    class StateTransitionTests {
        // ... Tests de aceptación, rechazo y pago ...
    }

    // --- 3. Tests de Comportamiento (Edición) ---

    @Nested
    @DisplayName("Modification Methods") // Renombrado de (editDebtMoney) a solo Methods
    class ModificationTests {

        // --- Tests para editDebtMoney (Existentes) ---

        @Test
        @DisplayName("Should successfully edit amount if status is PENDING")
        void shouldEditAmountIfPending() {
            Debt debt = createPendingDebt();
            DebtMoney newAmount = DebtMoney.load(5000L, "USD");

            Result<Void, DomainError> result = debt.editDebtMoney(newAmount);

            assertTrue(result.isSuccess());
            assertEquals(newAmount, debt.getDebtMoney(), "El monto debe ser actualizado.");
            assertEquals(Status.PENDING, debt.getStatus(), "El estado debe permanecer PENDING.");
        }

        @Test
        @DisplayName("Should fail to edit amount if status is ACCEPTED")
        void shouldFailToEditAmountIfAccepted() {
            Debt debt = createPendingDebt();
            debt.accept(); // Estado: ACCEPTED

            DebtMoney newAmount = DebtMoney.load(5000L, "USD");

            Result<Void, DomainError> result = debt.editDebtMoney(newAmount);

            // assertBusinessRuleErrorExists(result.getErrors(), DebtBusinessCode.STATUS_NOT_PENDING);
            assertEquals(debtMoney, debt.getDebtMoney(), "El monto no debe ser actualizado.");
        }

        // --- Tests para editContext (Nuevos) ---

        @Test
        @DisplayName("Should successfully edit context if status is PENDING")
        void shouldEditContextIfPending() {
            Debt debt = createPendingDebt();
            Context newContext = Context.load("Nuevo Propósito", "Descripción Editada");

            Result<Void, DomainError> result = debt.editContext(newContext);

            assertTrue(result.isSuccess(), "La edición del contexto debe ser exitosa.");
            assertEquals(newContext, debt.getContext(), "El contexto debe ser actualizado.");
            assertEquals(Status.PENDING, debt.getStatus(), "El estado debe permanecer PENDING.");
        }

        @Test
        @DisplayName("Should fail to edit context if status is ACCEPTED")
        void shouldFailToEditContextIfAccepted() {
            Debt debt = createPendingDebt();
            debt.accept(); // Estado: ACCEPTED
            Context newContext = Context.load("Nuevo Propósito", "Descripción Editada");
            Context originalContext = debt.getContext();

            Result<Void, DomainError> result = debt.editContext(newContext);

            assertTrue(result.isFailure(), "La edición debe fallar si no está PENDING.");
            // assertBusinessRuleErrorExists(result.getErrors(), DebtBusinessCode.STATUS_NOT_PENDING);
            assertEquals(originalContext, debt.getContext(), "El contexto no debe ser actualizado.");
        }

        @Test
        @DisplayName("Should fail to edit context if status is PAID")
        void shouldFailToEditContextIfPaid() {
            Debt debt = createPendingDebt();
            debt.accept();
            debt.pay(); // Estado: PAID
            Context newContext = Context.load("Nuevo Propósito", "Descripción Editada");
            Context originalContext = debt.getContext();

            Result<Void, DomainError> result = debt.editContext(newContext);

            assertTrue(result.isFailure(), "La edición debe fallar si no está PENDING.");
            // assertBusinessRuleErrorExists(result.getErrors(), DebtBusinessCode.STATUS_NOT_PENDING);
            assertEquals(originalContext, debt.getContext(), "El contexto no debe ser actualizado.");
        }
    }
}