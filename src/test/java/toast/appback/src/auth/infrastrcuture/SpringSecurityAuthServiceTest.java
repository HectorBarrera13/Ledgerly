package toast.appback.src.auth.infrastrcuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.infrastructure.exceptions.AuthenticationServiceException;
import toast.appback.src.auth.infrastructure.service.SpringSecurityAuthService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                TestSecurityConfig.class,
                SpringSecurityAuthService.class
        })
@ActiveProfiles("test")
@DisplayName("AuthService integration tests")
public class SpringSecurityAuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void shouldAuthenticateSuccessfully() {
        var command = new AuthenticateAccountCommand("test@example.com", "password123");
        assertDoesNotThrow(() -> authService.authenticate(command));
    }

    @Test
    void shouldFailWithInvalidCredentials() {
        var command = new AuthenticateAccountCommand("test@example.com", "wrongPassword");
        assertThrows(AuthenticationServiceException.class, () -> authService.authenticate(command));
    }

}
