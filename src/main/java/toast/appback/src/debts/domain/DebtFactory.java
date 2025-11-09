package toast.appback.src.debts.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.domain.User;

import java.util.UUID;

public abstract class DebtFactory {
    public abstract Result<Debt, DomainError> create(
        DebtId id,
        User debtor,
        User creditor,
        Status status
    );

    public Result<Debt, DomainError> create(
        User debtor,
        User creditor
    ){
        return create(DebtId.generateDebtId(), debtor, creditor, new Status("Enviada"));
    }

}
