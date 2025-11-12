package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.infrastructure.exceptions.AuthenticationServiceException;

@Service
@RequiredArgsConstructor
public class SpringSecurityAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;

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
