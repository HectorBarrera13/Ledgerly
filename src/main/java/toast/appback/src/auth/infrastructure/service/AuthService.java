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
import toast.appback.src.users.application.usecase.contract.CreateUser;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegisterAccount registerAccount;

    private final AccountLogin accountLogin;

    private final AccountLogout accountLogout;

    private final RefreshSession refreshSession;

    @Transactional
    public RegisterAccountResult registerAccount(RegisterAccountCommand command) {
        return registerAccount.register(command).mapResult(ErrorsProxy::handleResult);
    }

    @Transactional
    public TokenInfo loginAccount(AccountAuthCommand command) {
        return accountLogin.login(command).mapResult(ErrorsProxy::handleResult);
    }

    @Transactional
    public void logoutAccount(String token) {
        accountLogout.logout(token).mapResult(ErrorsProxy::handleResult);
    }

    public TokenInfo refreshSession(String refreshToken) {
        return refreshSession.refresh(refreshToken).mapResult(ErrorsProxy::handleResult);
    }
}
