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
 * Caso de uso para confirmar el pago reportado de una DebtBetweenUsers.
 *
 * Este caso de uso sigue el patrón de Application Service dentro de Clean Architecture.
 *
 * Responsabilidades:
 * 1. Recuperar datos desde los repositorios (Debt y Users)
 * 2. Validar reglas de autorización (solo el acreedor puede confirmar)
 * 3. Ejecutar la lógica de dominio (confirmPayment())
 * 4. Persistir cambios en el repositorio
 * 5. Publicar los eventos de dominio generados por la entidad
 * 6. Construir y retornar el DTO de lectura (DebtBetweenUsersView)
 */
public class ConfirmDebtPaymentUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Constructor que recibe todas sus dependencias mediante inyección.
     * Sigue el principio de inversión de dependencias (DIP).
     */
    public ConfirmDebtPaymentUseCase(
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
     * 1. Buscar la deuda por ID
     * 2. Validar que el actor sea el acreedor
     * 3. Recuperar debtor y creditor desde UserRepository
     * 4. Ejecutar el método de dominio confirmPayment()
     * 5. Persistir cambios
     * 6. Publicar eventos
     * 7. Retornar DTO de salida
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Recuperar la deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Validar autorización: SOLO el acreedor puede confirmar un pago
        boolean isActorTheCreditor = command.actorId().equals(debt.getCreditorId());
        if (!isActorTheCreditor) {
            throw new UnauthorizedActionException("You are not authorized to perform this action");
        }

        // 3) Recuperar datos del deudor
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        // Recuperar datos del acreedor
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // Obtenemos los nombres para los DTOs
        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

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
        Result<Void, DomainError> result = debt.confirmPayment();
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 5) Guardar cambios
        debtRepository.save(debt);

        // 6) Publicar eventos de dominio generados
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Retornar DTO con los datos actualizados
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
