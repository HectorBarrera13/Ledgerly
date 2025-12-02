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
 * Caso de uso para que el deudor reporte que ya realizó el pago de la deuda.
 *
 * Patrón: Application Service (Clean Architecture).
 *
 * Responsabilidades:
 *  1. Recuperar la entidad DebtBetweenUsers desde el repositorio.
 *  2. Validar autorización: solo el deudor puede reportar el pago.
 *  3. Recuperar información del deudor y acreedor (para el DTO de salida).
 *  4. Ejecutar la lógica del dominio: debt.reportPayment().
 *  5. Manejar errores del dominio.
 *  6. Persistir la deuda actualizada.
 *  7. Publicar eventos generados por el agregado.
 *  8. Retornar una vista de lectura con los datos actualizados.
 */
public class ReportDebtPaymentUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Dependencias inyectadas por constructor siguiendo DIP.
     */
    public ReportDebtPaymentUseCase(
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
     * 2. Verificar que el actor sea el deudor
     * 3. Recuperar debtor y creditor
     * 4. Ejecutar lógica de dominio (reportPayment)
     * 5. Lanzar excepción si falla
     * 6. Guardar deuda
     * 7. Publicar eventos
     * 8. Retornar DTO actualizado
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Buscar deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Validación de autorización
        boolean isActorTheDebtor = command.actorId().equals(debt.getDebtorId());
        if (!isActorTheDebtor) {
            throw new UnauthorizedActionException("User is not the debtor");
        }

        // 3) Recuperar usuarios
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // 4) Construcción de DTOs
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

        // 5) Lógica de dominio
        Result<Void, DomainError> result = debt.reportPayment();
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 6) Persistencia
        debtRepository.save(debt);

        // 7) Publicación de eventos
        domainEventBus.publishAll(debt.pullEvents());

        // 8) Retorno del DTO final
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