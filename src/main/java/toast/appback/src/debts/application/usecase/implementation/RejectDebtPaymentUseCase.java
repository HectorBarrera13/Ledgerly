package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.contract.EditDebtStatus;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.repository.UserRepository;

public class RejectDebtPaymentUseCase implements EditDebtStatus {
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;

    public RejectDebtPaymentUseCase(DebtRepository debtRepository, DomainEventBus domainEventBus) {
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public DebtView execute(EditDebtStatusCommand command) {
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        boolean isActorTheCreditor = command.actorId().equals(debt.getCreditorId());
        if(!isActorTheCreditor){
            throw new UnauthorizedActionException("User is not the creditor");
        }

        Result<Void, DomainError> result = debt.rejectPayment();

        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        debtRepository.save(debt);

        domainEventBus.publishAll(debt.pullEvents());

        return new DebtView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount().longValue(),
                debt.getDebtMoney().getCurrency(),
                debt.getDebtorName(),
                debt.getCreditorName(),
                debt.getStatus().toString()
        );
    }

}
