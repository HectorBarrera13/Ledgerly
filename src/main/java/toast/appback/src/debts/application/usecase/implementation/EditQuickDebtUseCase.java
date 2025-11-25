package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.contract.EditDebt;
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

public class EditQuickDebtUseCase implements EditDebt {
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;
    private final UserRepository userRepository;
    private final String DEBTOR = "DEBTOR";
    private final String CREDITOR = "CREDITOR";

    public EditQuickDebtUseCase(DebtRepository debtRepository, DomainEventBus domainEventBus, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
        this.userRepository = userRepository;
    }

    @Override
    public QuickDebtView execute(EditDebtCommand command) {
        QuickDebt debt = debtRepository.findQuickDebtById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        Result<DebtMoney, DomainError> moneyResult = DebtMoney.create(command.newCurrency(), command.newAmount());
        Result<Context, DomainError> contextResult = Context.create(command.newPurpose(), command.newDescription());

        Result<Void, DomainError> validationResult = Result.empty();
        validationResult.collect(moneyResult);
        validationResult.collect(contextResult);
        validationResult.ifFailureThrows(EditDebtException::new);
        DebtMoney newMoney = moneyResult.get();
        Context newContext = contextResult.get();

        debt.editDebtMoney(newMoney)
                .orElseThrow(EditDebtException::new);
        debt.editContext(newContext)
                .orElseThrow(EditDebtException::new);

        User user = userRepository.findById(debt.getUserId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));
        Name userName = user.getName();
        UserSummaryView userSummary = new UserSummaryView(
                user.getUserId().getValue(),
                userName.getFirstName(),
                userName.getLastName()
        );

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

