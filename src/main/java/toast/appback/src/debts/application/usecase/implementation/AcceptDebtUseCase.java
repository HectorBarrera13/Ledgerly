package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsersStatus;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Caso de uso encargado de aceptar una DebtBetweenUsers.
 *
 * Patrón:
 *  - Application Service (Capa de Aplicación)
 *
 * Responsabilidades:
 *  1) Recuperar entidades necesarias mediante repositorios
 *  2) Verificar reglas de autorización
 *  3) Ejecutar la lógica de dominio (accept())
 *  4) Persistir cambios
 *  5) Publicar eventos de dominio
 *  6) Retornar un DTO de lectura (View) para la capa superior
 *
 * No contiene lógica de negocio, solo orquestación.
 */
public class AcceptDebtUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Recibe todas las dependencias de la capa de infraestructura mediante inversión de dependencias.
     */
    public AcceptDebtUseCase(
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
     *  1. Buscar la deuda → lanzar DebtNotFound si no existe
     *  2. Validar que el actor sea el deudor → lanzar UnauthorizedActionException
     *  3. Recuperar debtor y creditor desde UserRepository
     *  4. Intentar aceptar la deuda → si falla, lanzar AcceptDebtException
     *  5. Guardar cambios
     *  6. Publicar eventos registrados en la entidad
     *  7. Retornar una vista (DTO) representando el resultado
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Recuperar la deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Reglas de autorización de negocio (actor debe ser el deudor)
        boolean isActorTheDebtor = command.actorId().equals(debt.getDebtorId());
        if (!isActorTheDebtor) {
            throw new UnauthorizedActionException("You are not authorized to perform this action");
        }

        // 3) Recuperar al deudor
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        // Recuperar al acreedor
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // Obtener nombres
        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

        // Crear DTOs de resumen (UserSummaryView)
        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(),
                debtorName.getFirstName(),
                debtorName.getLastName()
        );

        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(),
                creditorName.getFirstName(),
                creditorName.getLastName()
        );

        // 4) Ejecutar la lógica de dominio
        Result<Void, DomainError> result = debt.accept();
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 5) Guardar los cambios
        debtRepository.save(debt);

        // 6) Publicar eventos de dominio
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Construir la vista de salida
        return new DebtBetweenUsersView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount(),
                debt.getDebtMoney().getCurrency(),
                debt.getStatus().toString(),
                debtorSummary,
                creditorSummary
        );
    }
}
