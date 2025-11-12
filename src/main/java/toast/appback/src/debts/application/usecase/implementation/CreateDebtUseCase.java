package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.debts.application.communication.command.CreateDebtCommand;
import toast.appback.src.debts.application.usecase.contract.CreateDebt;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.middleware.ApplicationException;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
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
    public Debt execute(CreateDebtCommand command) throws ApplicationException {
        Optional<User> foundDebtor = userRepository.findById(command.debtorId());
        if (foundDebtor.isEmpty()) {
            ErrorsHandler.handleError(AppError.entityNotFound("User","User debtor not found"));
        }
        Optional<User> foundCreditor = userRepository.findById(command.creditorId());
        if (foundCreditor.isEmpty()) {
            ErrorsHandler.handleError(AppError.entityNotFound("User","User creditor not found"));
        }


        Result<Debt, DomainError> debt = Debt.create(command.purpose(),command.description(), command.currency(), command.amount(), foundCreditor.get(), foundDebtor.get());
        if(debt.isFailure()){
            ErrorsHandler.handleError(AppError.dataIntegrityViolation("Datos incorrectos"));
        }
        Debt newDebt = debt.getValue();

        debtRepository.save(newDebt);

        eventBus.publishAll(newDebt.pullEvents());

        return newDebt;
    }
}
