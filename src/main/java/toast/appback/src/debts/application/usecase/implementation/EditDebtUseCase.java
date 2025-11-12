package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.usecase.contract.EditDebt;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.DebtMoney;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.middleware.ApplicationException;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import java.util.Optional;

public class EditDebtUseCase implements EditDebt{
    private final EventBus eventBus;
    private final DebtRepository debtRepository;

    public EditDebtUseCase(DebtRepository debtRepository, EventBus eventBus) {
        this.eventBus = eventBus;
        this.debtRepository = debtRepository;
    }

    @Override
    public Debt execute(EditDebtCommand command) throws ApplicationException {
        Optional<Debt> foundDebt = debtRepository.findById(command.debtId());
        if(foundDebt.isEmpty()){
            ErrorsHandler.handleError(AppError.entityNotFound("Debt","Debt not found"));
        }
        Debt debt = foundDebt.get();

        Result<Context, DomainError> context = Context.create(command.purpose(), command.description());
        if(context.isFailure()){
            ErrorsHandler.handleError(AppError.dataIntegrityViolation("Datos incorrectos"));
        }
        Context newContext = context.getValue();

        Result<DebtMoney, DomainError> debtMonet = DebtMoney.create(command.currency(), command.amount());
        if(debtMonet.isFailure()){
            ErrorsHandler.handleError(AppError.dataIntegrityViolation("Datos incorrectos"));
        }
        DebtMoney newDebtMoney = debtMonet.getValue();

        debt.editDebtMoney(newDebtMoney);
        debt.editContext(newContext);

        debtRepository.save(debt);

        eventBus.publishAll(debt.pullEvents());

        return debt;
    }
}


