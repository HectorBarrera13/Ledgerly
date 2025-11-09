package toast.appback.src.debts.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.User;

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
        return create(DebtId.generate(), debtor, creditor, new Status("Enviada"));
    }

}
