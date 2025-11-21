package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.contract.EditDebt;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

public class EditDebtUseCase implements EditDebt{
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;
    private final String DEBTOR = "DEBTOR";
    private final String CREDITOR = "CREDITOR";

    public EditDebtUseCase(DebtRepository debtRepository, DomainEventBus domainEventBus) {
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public DebtView execute(EditDebtCommand command) {
        Debt debt = debtRepository.findDebtById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        DebtMoney newMoney = DebtMoney.create(command.newCurrency(), command.newAmount()).orElseThrow(CreationDebtException::new);
        Context newContext = Context.create(command.newPurpose(), command.newDescription()).orElseThrow(CreationDebtException::new);

        Result<Void, DomainError> editMoneyResult = debt.editDebtMoney(newMoney);
        Result<Void, DomainError> editContextResult = debt.editContext(newContext);

        Result<Void, DomainError> updateResult = Result.empty();
        updateResult.collect(editMoneyResult);
        updateResult.collect(editContextResult);

        if(updateResult.isFailure()){
            throw new EditDebtException(updateResult.getErrors());
        }

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

