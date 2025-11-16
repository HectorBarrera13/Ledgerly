package toast.appback.src.auth.application.mother;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.domain.Account;

public class AccountViewMother {
    public static AccountView create(Account account) {
        return new AccountView(
                account.getAccountId().getValue(),
                account.getEmail().getValue()
        );
    }
}
