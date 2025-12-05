package toast.appback.src.auth.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;

/**
 * Envoltura transaccional para el caso de uso de registro de cuenta.
 */
@Service
@RequiredArgsConstructor
public class RegisterAccountService {
    private final RegisterAccount registerAccount;

    /**
     * Ejecuta el registro de una cuenta.
     *
     * @param command el comando con los datos del registro
     * @return el resultado de la autenticación
     */
    @Transactional
    public AuthResult execute(RegisterAccountCommand command) {
        return registerAccount.execute(command);
    }
}
