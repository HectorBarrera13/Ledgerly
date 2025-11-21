package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateDebtBetweenUsersUseCase implements CreateDebtBetweenUsers {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;

    public CreateDebtBetweenUsersUseCase( UserRepository userRepository, DebtRepository debtRepository, DomainEventBus domainEventBus) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public DebtView execute(CreateDebtBetweenUsersCommand command) {
        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));

        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

        Context context = Context.create(command.purpose(), command.description()).orElseThrow(CreationDebtException::new);
        DebtMoney debtMoney = DebtMoney.create(command.currency(), command.amount()).orElseThrow(CreationDebtException::new);

        DebtBetweenUsers debt = DebtBetweenUsers.create(context, debtMoney,command.debtorId(), command.creditorId(), debtor.getName().toString(), creditor.getName().toString());

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
