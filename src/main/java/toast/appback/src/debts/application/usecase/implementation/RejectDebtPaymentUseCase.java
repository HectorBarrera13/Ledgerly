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
 * Caso de uso para que el acreedor rechace la confirmación de pago
 * realizada por el deudor en una DebtBetweenUsers.
 *
 * Patrón: Application Service (Clean Architecture).
 *
 * Responsabilidades del caso de uso:
 *  1. Recuperar la deuda por ID
 *  2. Validar autorización (solo el acreedor puede rechazar un pago)
 *  3. Recuperar debtor y creditor desde UserRepository (para DTO)
 *  4. Ejecutar la lógica de dominio: rejectPayment()
 *  5. Manejar errores del dominio
 *  6. Guardar la deuda modificada
 *  7. Publicar eventos de dominio generados
 *  8. Retornar un DTO de lectura (DebtBetweenUsersView)
 */
public class RejectDebtPaymentUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Dependencias inyectadas por constructor (DIP).
     */
    public RejectDebtPaymentUseCase(
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
     * 1. Buscar la deuda
     * 2. Validar que el actor sea el acreedor
     * 3. Recuperar debtor y creditor
     * 4. Ejecutar rejectPayment() del dominio
     * 5. Lanzar excepción si falla
     * 6. Guardar cambios y publicar eventos
     * 7. Retornar DTO
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Recuperar deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Validación de autorización
        boolean isActorTheCreditor = command.actorId().equals(debt.getCreditorId());
        if (!isActorTheCreditor) {
            throw new UnauthorizedActionException("User is not the creditor");
        }

        // 3) Recuperar debtor
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        // Recuperar creditor
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // Preparar resumen de usuarios
        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(), debtorName.getFirstName(), debtorName.getLastName()
        );

        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(), creditorName.getFirstName(), creditorName.getLastName()
        );

        // 4) Lógica de dominio
        Result<Void, DomainError> result = debt.rejectPayment();
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 5) Persistir cambios
        debtRepository.save(debt);

        // 6) Publicar eventos de dominio
        domainEventBus.publishAll(debt.pullEvents());

        // 7) DTO de retorno
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
