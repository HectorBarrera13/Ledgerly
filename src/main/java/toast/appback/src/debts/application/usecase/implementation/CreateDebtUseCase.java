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
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

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
        Optional<User> foundDebtor = userRepository.findById(command.debtorId());
        if (foundDebtor.isEmpty()) {
            throw new DebtorNotFound(command.debtorId().getValue());
        }
        Optional<User> foundCreditor = userRepository.findById(command.creditorId());
        if (foundCreditor.isEmpty()) {
            throw new CreditorNotFound(command.creditorId().getValue());
        }

        Result<Debt, DomainError> debt = Debt.create(command.purpose(),command.description(), command.currency(), command.amount(), foundCreditor.get(), foundDebtor.get());
        debt.ifFailureThrows(CreationDebtException::new);

        Debt newDebt = debt.getValue();

        debtRepository.save(newDebt);

        eventBus.publishAll(newDebt.pullEvents());

        return newDebt;
    }
}
