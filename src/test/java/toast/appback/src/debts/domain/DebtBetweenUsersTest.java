package toast.appback.src.debts.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Debt Aggregate Test")
public class DebtBetweenUsersTest {

    private DebtId debtId;
    private Context context;
    private DebtMoney debtMoney;
    private UserId debtorId;
    private UserId creditorId;

    public class testDomainEvent implements DomainEvent{

    }

    private static Long bigDecimalToLong(BigDecimal bigDecimal) {
        return bigDecimal.longValue()*100;
    }

    @BeforeEach
    void setUp() {
        debtId = DebtId.load(UUID.randomUUID());
        context = Context.load("Test Purpose", "Test Description");
        debtMoney = DebtMoney.load(1000L, "USD");

        UserId debtorId = UserId.load(UUID.randomUUID());
        String debtorName = "Debtor Test Name";

        UserId creditorId = UserId.load(UUID.randomUUID());
        String creditorName = "Creditor Test Name";
    }

    private DebtBetweenUsers createPendingDebt() {
        return DebtBetweenUsers.load(debtId, context, debtMoney, debtorId, creditorId, "John Doe", "Jane Smith");
    }


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
            DebtBetweenUsers debt = createPendingDebt();

            assertEquals(debtId, debt.getId());
            assertEquals(context, debt.getContext());
            assertEquals(debtMoney, debt.getDebtMoney()); // Asumo getDebtMoney() es el getter correcto
            assertEquals(debtorId, debt.getDebtorId());
            assertEquals(creditorId, debt.getCreditorId());
            // El estado inicial debe ser PENDING por defecto
            assertEquals(Status.PENDING, debt.getStatus());
            // La lista de eventos debe estar vacía
            assertNotNull(debt.getDebtEvents());
            assertTrue(debt.getDebtEvents().isEmpty());
        }

        @Test
        @DisplayName("Should initialize Debt correctly with existing events")
        void shouldInitializeDebtWithEvents() {

            DebtBetweenUsers debt = DebtBetweenUsers.create( context, debtMoney, debtorId, creditorId, "John Doe", "Jane Smith");

            assertEquals(1, debt.getDebtEvents().size());
        }
    }

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
            DebtId id = debtAggregate.getId();

            String purpose = "Comida";
            String description = "Cena de anoche";
            String currency = "MXN";
            Long amount = 5000L; // 50.00 MXN sin escalar

            Context context = Context.load(purpose, description);
            DebtMoney debtMoney = DebtMoney.load(amount, currency);

            DebtBetweenUsers createdDebt = DebtBetweenUsers.load(id, context, debtMoney, debtorId,creditorId, "Debtor Name", "Creditor Name");

            assertNotNull(createdDebt.getId(), "El ID debe ser generado.");

            assertEquals(creditorId, createdDebt.getCreditorId(), "El deudor debe ser el CREDITOR (según la lógica de Create).");
            assertEquals(debtorId, createdDebt.getDebtorId(), "El acreedor debe ser el DEBTOR (según la lógica de Create).");

            assertEquals(purpose, createdDebt.getContext().getPurpose(), "El propósito debe coincidir.");
            assertEquals(description, createdDebt.getContext().getDescription(), "La descripción debe coincidir.");
        }

    }



    @Nested
    @DisplayName("State Transition Methods")
    class StateTransitionTests {

    }


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

            assertTrue(result.isOk());
            assertEquals(newAmount, debt.getDebtMoney(), "El monto debe ser actualizado.");
            assertEquals(Status.PENDING, debt.getStatus(), "El estado debe permanecer PENDING.");
        }

        @Test
        @DisplayName("Should fail to edit amount if status is ACCEPTED")
        void shouldFailToEditAmountIfAccepted() {
            DebtBetweenUsers debt = createPendingDebt();
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

            assertTrue(result.isOk(), "La edición del contexto debe ser exitosa.");
            assertEquals(newContext, debt.getContext(), "El contexto debe ser actualizado.");
            assertEquals(Status.PENDING, debt.getStatus(), "El estado debe permanecer PENDING.");
        }

        @Test
        @DisplayName("Should fail to edit context if status is ACCEPTED")
        void shouldFailToEditContextIfAccepted() {
            DebtBetweenUsers debt = createPendingDebt();
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
            DebtBetweenUsers debt = createPendingDebt();
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