package toast.appback.src.debts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toast.appback.src.debts.application.communication.command.CreateDebtCommand;
import toast.appback.src.debts.application.usecase.implementation.CreateDebtUseCase;
import toast.appback.src.debts.domain.*;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.middleware.ApplicationException;
import toast.appback.src.shared.application.EventBus;

import toast.appback.src.shared.domain.DomainEvent;

import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Permite usar anotaciones @Mock y @InjectMocks
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDebt Use Case Test")
public class CreateDebtUseCaseTest {

    // Dependencias (Mocks)
    @Mock
    private EventBus eventBus;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DebtRepository debtRepository;
    // No se necesita mockear AuthService si no se usa en execute()

    // Clase a probar (se inyectan los mocks automáticamente)
    @InjectMocks
    private CreateDebtUseCase createDebtUseCase;

    // Datos de prueba
    private CreateDebtCommand validCommand;
    private User mockDebtor;
    private User mockCreditor;
    private UserId creditorId;
    private UserId debtorId;

    // Mock de la clase Debt para simular el éxito/fallo de su método estático 'create'
    // Como el método create es estático, lo simularemos indirectamente o asumiremos que funciona
    // y nos enfocaremos en las interacciones.

    @BeforeEach
    void setUp() {
        creditorId = UserId.load(UUID.randomUUID());
        debtorId = UserId.load(UUID.randomUUID());

        mockCreditor = mock(User.class);
        mockDebtor = mock(User.class);

        when(mockCreditor.getId()).thenReturn(creditorId);
        when(mockDebtor.getId()).thenReturn(debtorId);

        validCommand = new CreateDebtCommand(
                "Almuerzo",
                "Comida de negocios",
                "USD",
                5000L,
                debtorId,
                creditorId
        );

        // Configuración de Mocks estándar (caso de éxito)
        when(userRepository.findById(creditorId)).thenReturn(Optional.of(mockCreditor));
        when(userRepository.findById(debtorId)).thenReturn(Optional.of(mockDebtor));
    }

    @Test
    public void mockSuccessfulDebtCreation() {
        Debt mockDebt = createDebtUseCase.execute(validCommand);

        assertNotNull(mockDebt);
        verify(userRepository).findById(creditorId);
        verify(userRepository).findById(debtorId);
    }


    // --- 1. Caso de Éxito ---

    @Test
    @DisplayName("Should successfully create debt, save it, and publish events")
    void shouldCreateSaveAndPublishSuccessfully() throws ApplicationException {
        // 1. Preparación de la creación exitosa del dominio
        // Creamos un Debt que simula el resultado del factory method
        Debt createdDebt = mock(Debt.class);
        List<DomainEvent> domainEvents = List.of(new DomainEvent() {
        }); // Simular que hay eventos
        when(createdDebt.pullEvents()).thenReturn(domainEvents);

        Debt resultDebt = createDebtUseCase.execute(validCommand);

        // 2. Verificación de la Persistencia
        // Verificamos que se llamó a save con el objeto Debt retornado por Debt.create
        verify(debtRepository, times(1)).save(any(Debt.class));

        // 3. Verificación de Eventos
        // Verificamos que se llamó a publishAll con los eventos extraídos de la Debt
        verify(eventBus, times(1)).publishAll(anyList()); // Mejor usar anyList() ya que no podemos pasar 'domainEvents' directamente

        // 4. Verificación del Resultado
        assertNotNull(resultDebt, "El caso de uso debe retornar una Debt no nula.");
        // Si no podemos mockear el objeto Debt, no podemos verificar el ID, pero verificamos que se devolvió algo.
    }

    // --- 2. Casos de Fallo (Dependencias) ---

    @Test
    @DisplayName("Should throw ApplicationException if Creditor is not found")
    void shouldFailIfCreditorNotFound() {
        // 1. Configuración: No encontrar al Creditor
        when(userRepository.findById(creditorId)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            createDebtUseCase.execute(validCommand);
        }, "Debe lanzar ApplicationException al no encontrar el Creditor.");

        // Verificación del error específico (asumiendo que AppError.entityNotFound existe)
        System.out.println(thrown.getMessage().contains("User creditor not found"));
        assertTrue(thrown.getMessage().contains("User creditor not found"));

        // 3. Verificación de Interacciones (No debe llegar a salvar/publicar)
        verify(userRepository, never()).findById(debtorId); // Corta antes
        verify(debtRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    @Test
    @DisplayName("Should throw ApplicationException if Debtor is not found")
    void shouldFailIfDebtorNotFound() {
        // 1. Configuración: Encontrar Creditor, pero no Debtor
        when(userRepository.findById(debtorId)).thenReturn(Optional.empty());

        // 2. Ejecución y Verificación
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            createDebtUseCase.execute(validCommand);
        }, "Debe lanzar ApplicationException al no encontrar el Debtor.");

        // Verificación del error específico
        assertTrue(thrown.getMessage().contains("User debtor not found"));

        // 3. Verificación de Interacciones (No debe llegar a salvar/publicar)
        verify(userRepository, times(1)).findById(creditorId); // Ya se verificó
        verify(debtRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    // --- 3. Casos de Fallo (Dominio) ---

    @Test
    @DisplayName("Should throw ApplicationException if Debt.create returns a failure (DomainError)")
    void shouldFailIfDebtCreationFails() {
        // Este test es el más complicado debido al método estático `Debt.create`.
        // Para simular el fallo, necesitamos que la llamada a `Debt.create` falle.

        // **Asunción clave:** Para este test se necesita una solución de mocking avanzada
        // (como PowerMock) para simular que `Debt.create(...)` retorna un `Result.failure`.

        // Dado que solo usamos Mockito, la alternativa es:

        // A) Refactorizar `Debt.create` a un servicio/factory inyectable (Recomendado).
        // B) Usar datos inválidos que *sabemos* causarán un fallo real en el dominio.

        // Usaremos la opción B: Usar un monto que sabemos es inválido (ej. 0 o negativo)
        // y confiamos en que el test de dominio (`DebtTest`) ya verificó que falla.
        CreateDebtCommand invalidCommand = new CreateDebtCommand(
                "Almuerzo",
                "Comida de negocios",
                "USD",
                -100L, // Monto inválido que DEBE causar DomainError en DebtMoney.create
                creditorId,
                debtorId
        );

        // Ejecución y Verificación
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> {
            createDebtUseCase.execute(invalidCommand);
        }, "Debe lanzar ApplicationException si el dominio falla en la creación.");

        // Verificación del error específico
        assertTrue(thrown.getMessage().contains("Datos incorrectos"));

        // Verificación de Interacciones (No debe llegar a salvar/publicar)
        verify(debtRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }
}