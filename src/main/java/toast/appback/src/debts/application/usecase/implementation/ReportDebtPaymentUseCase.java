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
 * Caso de uso que reporta la realización de un pago por parte del deudor.
 *
 * <p>El actor que reporta debe ser el deudor; el agregado validará reglas de negocio y
 * publicará eventos relacionados con la confirmación del pago.
 */
public class ReportDebtPaymentUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

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
     * Ejecuta el reporte de pago para una deuda.
     *
     * @param command Comando con la deuda y el actor que reporta.
     * @return {@link DebtBetweenUsersView} con el estado actualizado.
     * @throws DebtNotFound                Si la deuda no existe.
     * @throws UnauthorizedActionException Si el actor no es el deudor.
     * @throws AcceptDebtException         Si la regla del dominio impide el reporte.
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {
        //Comprobar que la deuda existe
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        boolean isActorTheDebtor = command.actorId().equals(debt.getDebtorId());
        if (!isActorTheDebtor) {
            throw new UnauthorizedActionException("User is not the debtor");
        }

        //Recuperar debtor y creditor
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

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

        Result<Void, DomainError> result = debt.reportPayment();
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        debtRepository.save(debt);

        domainEventBus.publishAll(debt.pullEvents());

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