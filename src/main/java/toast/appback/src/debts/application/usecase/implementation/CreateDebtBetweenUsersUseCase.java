package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateDebtBetweenUsersUseCase implements CreateDebtBetweenUsers {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;

    public CreateDebtBetweenUsersUseCase(UserRepository userRepository, DebtRepository debtRepository, DomainEventBus domainEventBus) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public DebtBetweenUsersView execute(CreateDebtBetweenUsersCommand command) {
        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));

        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

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

        Result<Void, DomainError> validationResult = Result.empty();
        Result<Context, DomainError> contextResult = Context.create(command.purpose(), command.description());
        Result<DebtMoney, DomainError> debtMoneyResult = DebtMoney.create(command.currency(), command.amount());
        validationResult.collect(contextResult);
        validationResult.collect(debtMoneyResult);
        validationResult.ifFailureThrows(CreationDebtException::new);

        Context context = contextResult.get();
        DebtMoney debtMoney = debtMoneyResult.get();
        DebtBetweenUsers debt = DebtBetweenUsers.create(context, debtMoney, command.debtorId(), command.creditorId());

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
