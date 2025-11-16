package toast.appback.src.auth.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;

@Service
@RequiredArgsConstructor
public class RegisterAccountService {
    private final RegisterAccount registerAccount;

    @Transactional
    public AuthResult execute(RegisterAccountCommand command) {
        return registerAccount.execute(command);
    }
}
