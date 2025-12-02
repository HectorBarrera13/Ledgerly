package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.contract.EditQuickDebt;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Caso de uso responsable de editar una QuickDebt.
 *
 * Patrón: Application Service (Capa de Aplicación).
 *
 * Responsabilidades:
 *  1. Recuperar la deuda rápida (QuickDebt) desde el repositorio.
 *  2. Validar los nuevos Value Objects (Context y DebtMoney).
 *  3. Aplicar reglas de dominio editando el monto y el contexto.
 *     - Si la deuda NO está en estado PENDING, el dominio devuelve error.
 *  4. Recuperar información del usuario para construir el DTO de salida.
 *  5. Persistir la deuda editada.
 *  6. Publicar eventos de dominio (si los hubiera).
 *  7. Retornar un DTO de lectura (QuickDebtView).
 *
 * Nota: Este caso de uso no contiene lógica de negocio.
 * Solo orquesta el flujo entre repositorios, dominio y DTOs.
 */
public class EditQuickDebtUseCase implements EditQuickDebt {

    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;
    private final UserRepository userRepository;

    /**
     * Las dependencias se inyectan mediante constructor siguiendo DIP.
     */
    public EditQuickDebtUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus,
            UserRepository userRepository
    ) {
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
        this.userRepository = userRepository;
    }

    /**
     * Flujo del caso de uso:
     *
     * 1. Buscar la QuickDebt → lanzar DebtNotFound si no existe
     * 2. Crear y validar nuevos Value Objects (Context, DebtMoney)
     * 3. Intentar editarlos en la entidad (reglas de dominio)
     * 4. Buscar el usuario creador para el DTO final
     * 5. Guardar cambios en el repositorio
     * 6. Publicar eventos de dominio
     * 7. Retornar QuickDebtView
     */
    @Override
    public QuickDebtView execute(EditDebtCommand command) {

        // 1) Recuperar QuickDebt
        QuickDebt debt = debtRepository.findQuickDebtById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Validación de nuevos VOs
        Result<DebtMoney, DomainError> moneyResult =
                DebtMoney.create(command.newCurrency(), command.newAmount());

        Result<Context, DomainError> contextResult =
                Context.create(command.newPurpose(), command.newDescription());

        Result<Void, DomainError> validationResult = Result.empty();
        validationResult.collect(moneyResult);
        validationResult.collect(contextResult);
        validationResult.ifFailureThrows(EditDebtException::new);

        DebtMoney newMoney = moneyResult.get();
        Context newContext = contextResult.get();

        // 3) Reglas de dominio: Solo se puede editar si está PENDING
        debt.editDebtMoney(newMoney).orElseThrow(EditDebtException::new);
        debt.editContext(newContext).orElseThrow(EditDebtException::new);

        // 4) Buscar al usuario creador (para DTO)
        User user = userRepository.findById(debt.getUserId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        Name userName = user.getName();
        UserSummaryView userSummary = new UserSummaryView(
                user.getUserId().getValue(),
                userName.getFirstName(),
                userName.getLastName()
        );

        // 5) Guardar cambios
        debtRepository.save(debt);

        // 6) Publicar eventos
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Retornar DTO
        return new QuickDebtView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount(),
                debt.getDebtMoney().getCurrency(),
                debt.getStatus().name(),
                userSummary,
                debt.getRole().getRole(),
                debt.getTargetUser().getName()
        );
    }
}
