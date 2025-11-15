package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.contract.EditDebt;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.DebtMoney;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

import java.util.Optional;

public class EditDebtUseCase implements EditDebt{
    private final EventBus eventBus;
    private final DebtRepository debtRepository;

    public EditDebtUseCase(DebtRepository debtRepository, EventBus eventBus) {
        this.eventBus = eventBus;
        this.debtRepository = debtRepository;
    }

    @Override
    public Debt execute(EditDebtCommand command) {
        Optional<Debt> foundDebt = debtRepository.findById(command.debtId());
        if(foundDebt.isEmpty()){
            throw new DebtNotFound(command.debtId().getValue());
        }
        Debt debt = foundDebt.get();

        Result<Context, DomainError> contextResult = Context.create(command.purpose(), command.description());
        Result<DebtMoney, DomainError> debtMoneyResult = DebtMoney.create(command.currency(), command.amount());
        Result<Void, DomainError> updateResult = Result.empty();
        updateResult.collect(contextResult);
        updateResult.collect(debtMoneyResult);
        updateResult.ifFailureThrows(EditDebtException::new);
        debtRepository.save(debt);
        eventBus.publishAll(debt.pullEvents());
        return debt;
    }
}


