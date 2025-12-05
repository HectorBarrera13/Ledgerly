package toast.appback.src.auth.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;

/**
 * Envoltura transaccional para el caso de uso de terminación de sesión.
 */
@Service
@RequiredArgsConstructor
public class TerminateSessionService {
    private final TerminateSession terminateSession;

    /**
     * Ejecuta la terminación de sesión.
     *
     * @param accessToken el token de acceso de la sesión a terminar
     */
    @Transactional
    public void execute(String accessToken) {
        terminateSession.execute(accessToken);
    }
}
