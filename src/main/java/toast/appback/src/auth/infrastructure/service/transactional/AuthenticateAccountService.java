package toast.appback.src.auth.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.usecase.contract.AuthenticateAccount;

/**
 * Envoltura transaccional para el caso de uso de autenticación de cuenta.
 * <p>
 * Garantiza que la operación se ejecute dentro de una transacción.
 */
@Service
@RequiredArgsConstructor
public class AuthenticateAccountService {
    private final AuthenticateAccount authenticateAccount;

    @Transactional
    public AuthResult execute(AuthenticateAccountCommand authenticateAccountCommand) {
        return authenticateAccount.execute(authenticateAccountCommand);
    }
}
