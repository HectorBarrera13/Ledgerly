package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;

import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateDebtBetweenUsersUseCase implements CreateDebtBetweenUsers {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;

    public CreateDebtBetweenUsersUseCase( UserRepository userRepository, DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }


    @Override
    public DebtBetweenUsers execute(CreateDebtBetweenUsersCommand command) {

        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));

        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

        DebtId id = DebtId.generate();
        Context context = Context.create(command.purpose(), command.description()).orElseThrow(CreationDebtException::new);
        DebtMoney debtMoney = DebtMoney.create(command.currency(), command.amount()).orElseThrow(CreationDebtException::new);

        DebtBetweenUsers debt = new DebtBetweenUsers(id,context, debtMoney,command.debtorId(), command.creditorId());

        debtRepository.save(debt);

        return debt;
    }
}
