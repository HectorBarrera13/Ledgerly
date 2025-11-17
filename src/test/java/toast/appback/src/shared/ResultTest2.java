package toast.appback.src.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

@DisplayName("Result Test 2")
public class ResultTest2 {
    private final String purpose = "Testing";
    private final String description = "Testing the Result chaining functionality";
    private final String currency = "USD";
    private final Long amount = 1000L;
    private final UserId debtorId = UserId.generate();
    private final UserId creditorId = UserId.generate();

    private final String invalidPurpose = "";
    private final String invalidDescription = "D".repeat(301);
    private final String invalidCurrency = "";
    private final Long invalidAmount = -500L;
    private final User invalidDebtor = null;
    private final User invalidCreditor = null;

    @Test
    @DisplayName("Valid Result chain test")
    public void validResultChainTest() {
        Result<Debt, DomainError> result = Result.<DomainError>chain()
                .and(() -> Context.create(purpose, description))
                .and(() -> DebtMoney.create(currency, amount))
                .result((context, debtMoney) -> new DebtBetweenUsers(
                        DebtId.generate(),
                        context,
                        debtMoney,
                        debtorId,
                        creditorId
                ));
        assert result.isOk();
    }
}
