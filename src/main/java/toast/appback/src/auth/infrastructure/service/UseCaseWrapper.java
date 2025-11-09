package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.application.usecase.contract.AccountLogin;
import toast.appback.src.auth.application.usecase.contract.AccountLogout;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;
import toast.appback.src.middleware.ErrorsProxy;

@Service
@RequiredArgsConstructor
public class UseCaseWrapper {

    private final RegisterAccount registerAccount;

    private final AccountLogin accountLogin;

    private final AccountLogout accountLogout;

    private final RefreshSession refreshSession;

    @Transactional
    public RegisterAccountResult registerAccount(RegisterAccountCommand command) {
        return registerAccount.execute(command).mapResult(ErrorsProxy::handleResult);
    }

    @Transactional
    public TokenInfo loginAccount(AccountAuthCommand command) {
        return accountLogin.execute(command).mapResult(ErrorsProxy::handleResult);
    }

    @Transactional
    public void logoutAccount(String accessToken) {
        accountLogout.execute(accessToken).mapResult(ErrorsProxy::handleResult);
    }

    public TokenInfo refreshSession(String accessToken) {
        return refreshSession.execute(accessToken).mapResult(ErrorsProxy::handleResult);
    }
}
