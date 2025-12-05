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
 * Caso de uso que rechaza una deuda entre usuarios.
 *
 * <p>El actor que rechaza debe ser el deudor. Valida reglas de negocio y publica eventos.
 */
public class RejectDebtUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    public RejectDebtUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Ejecuta el rechazo de la deuda indicada.
     *
     * @param command Comando con el identificador de la deuda y el actor.
     * @return Vista {@link DebtBetweenUsersView} con el estado actualizado.
     * @throws DebtNotFound                Si la deuda no existe.
     * @throws UnauthorizedActionException Si el actor no tiene permisos.
     * @throws AcceptDebtException         Si la regla de negocio impide el rechazo.
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {
        //Comprobar que la deuda existe
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        boolean isActorTheDebtor = command.actorId().equals(debt.getDebtorId());
        if (!isActorTheDebtor) {
            throw new UnauthorizedActionException("User is not the creditor");
        }


        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        Name debtorName = debtor.getName();
        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(),
                debtorName.getFirstName(),
                debtorName.getLastName()
        );

        Name creditorName = creditor.getName();
        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(),
                creditorName.getFirstName(),
                creditorName.getLastName()
        );

        Result<Void, DomainError> result = debt.reject();

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
