package toast.appback.src.debts.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.User;

public abstract class DebtFactory {
    public abstract Result<Debt, DomainError> create(
        String purpose,
        String description,
        Long amount,
        String currency,
        User debtor,
        User creditor
    );



}
