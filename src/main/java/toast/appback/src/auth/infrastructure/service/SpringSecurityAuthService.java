package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.infrastructure.exceptions.AuthenticationServiceException;

/**
 * Servicio que delega la autenticación al {@link AuthenticationManager} de Spring Security.
 * <p>
 * Uso:
 * - Recibe un {@link AuthenticateAccountCommand} y delega la autenticación.
 * - En caso de error, envuelve la excepción en {@link AuthenticationServiceException}.
 */
@Service
@RequiredArgsConstructor
public class SpringSecurityAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;

    /**
     * Autentica las credenciales proporcionadas.
     *
     * @param command Comando con email y password.
     * @throws AuthenticationServiceException si la autenticación falla por cualquier motivo.
     */
    @Override
    public void authenticate(AuthenticateAccountCommand command) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            command.email(),
                            command.password()
                    )
            );
        } catch (Exception e) {
            throw new AuthenticationServiceException(e);
        }
    }
}
