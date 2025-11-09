package toast.appback.src.quickDebt.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;

import toast.appback.src.users.domain.User;

public abstract class QuickDebtFactory {
    public abstract Result<QuickDebt, DomainError> create(
        QuickDebtId id,
        User registeredUser,
        Role registeredUserRole,
        String unregisteredUserName //Cambiar nombre??
    );

    public Result<QuickDebt, DomainError> create(
            User registeredUser,
            Role registeredUserRole,
            String unregisteredUserName
    ){
        return create(QuickDebtId.generateQuickDebtId(), registeredUser, registeredUserRole, unregisteredUserName);
    }
}
