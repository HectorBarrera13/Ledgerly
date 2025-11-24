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

public class SettleQuickDebtUseCase implements EditQuickDebtStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    public SettleQuickDebtUseCase(DebtRepository debtRepository,UserRepository userRepository, DomainEventBus domainEventBus) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.domainEventBus = domainEventBus;
    }


    @Override
    public QuickDebtView execute(EditDebtStatusCommand command) {
        QuickDebt debt = debtRepository.findQuickDebtById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        User user = userRepository.findById(command.actorId())
                .orElseThrow(() -> new DebtorNotFound(command.actorId().getValue()));

        UserSummaryView userSummary = new UserSummaryView(
                user.getUserId().getValue(),
                user.getName().getFirstName(),
                user.getName().getLastName()
        );

        Result<Void, DomainError> result = debt.pay();

        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        debtRepository.save(debt);

        domainEventBus.publishAll(debt.pullEvents());

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
