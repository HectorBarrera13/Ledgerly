package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.debts.application.communication.command.CreateDebtCommand;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.CreateDebt;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateDebtUseCase implements CreateDebt {

    private final EventBus eventBus;
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;


    public CreateDebtUseCase(EventBus eventBus, UserRepository userRepository, DebtRepository debtRepository, AuthService authService) {
        this.eventBus = eventBus;
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }

    @Override
    public Debt execute(CreateDebtCommand command) {
        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));

        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

        Debt debt = Debt.create(
                command.purpose(),command.description(),
                command.currency(),
                command.amount(),
                creditor,
                debtor
        ).orElseThrow(CreationDebtException::new);

        debtRepository.save(debt);

        eventBus.publishAll(debt.pullEvents());

        return debt;
    }
}
