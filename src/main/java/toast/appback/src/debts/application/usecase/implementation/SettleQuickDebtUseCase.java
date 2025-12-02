package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.EditQuickDebtStatus;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Caso de uso para liquidar (settle) una QuickDebt.
 *
 * Patrón: Application Service (Clean Architecture).
 *
 * Responsabilidades:
 *  1. Recuperar la deuda desde el repositorio
 *  2. Validar que el actor exista en el sistema
 *  3. Ejecutar la lógica del dominio: debt.pay()
 *  4. Manejar errores de negocio del dominio
 *  5. Persistir el estado actualizado de la deuda
 *  6. Publicar los eventos de dominio generados
 *  7. Retornar un DTO QuickDebtView con el estado final
 *
 * Este caso de uso no contiene reglas del dominio.
 * Su única responsabilidad es coordinar la operación.
 */
public class SettleQuickDebtUseCase implements EditQuickDebtStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Se inyectan dependencias mediante constructor
     * siguiendo el principio de inversión de dependencias (DIP).
     */
    public SettleQuickDebtUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Flujo del caso de uso:
     *
     * 1. Buscar la QuickDebt en el repositorio
     * 2. Buscar al usuario actor (solo para mostrarlo en el DTO)
     * 3. Ejecutar lógica de dominio → debt.pay()
     * 4. Verificar si hubo errores
     * 5. Persistir cambios y publicar eventos
     * 6. Retornar la vista de la deuda con sus datos finales
     */
    @Override
    public QuickDebtView execute(EditDebtStatusCommand command) {

        // 1) Recuperar la deuda o lanzar error
        QuickDebt debt = debtRepository.findQuickDebtById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Recuperar usuario actor
        User user = userRepository.findById(command.actorId())
                .orElseThrow(() -> new DebtorNotFound(command.actorId().getValue()));

        // Crear resumen de usuario para DTO
        UserSummaryView userSummary = new UserSummaryView(
                user.getUserId().getValue(),
                user.getName().getFirstName(),
                user.getName().getLastName()
        );

        // 3) Ejecutar lógica del dominio
        Result<Void, DomainError> result = debt.pay();

        // 4) Validar error de reglas del dominio
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 5) Persistir estado
        debtRepository.save(debt);

        // 6) Publicar eventos
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Armar DTO final
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