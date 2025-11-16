package toast.appback.src.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.implementation.*;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.domain.DefaultAccount;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.application.usecase.contract.CreateUser;

@Configuration
public class AuthUseCasesConfig {

    @Bean
    public DefaultAccount defaultAccount(
            PasswordHasher passwordHasher
    ) {
        return new DefaultAccount(passwordHasher);
    }

    @Bean
    public CreateAccountUseCase createAccount(
            AccountRepository accountRepository,
            AccountFactory accountFactory
    ) {
        return new CreateAccountUseCase(
                accountRepository,
                accountFactory
        );
    }


    @Bean
    public RegisterAccountUseCase registerAccountUseCase(
            CreateAccount createAccount,
            CreateUser createUser,
            TokenService tokenService,
            DomainEventBus domainEventBus
    ) {
        return new RegisterAccountUseCase(
                createUser,
                createAccount,
                tokenService,
                domainEventBus
        );
    }

    @Bean
    public AuthenticateAccountUseCase accountLoginUseCase(
            TokenService tokenService,
            AuthService authService,
            AccountRepository accountRepository,
            UserReadRepository userReadRepository,
            DomainEventBus domainEventBus
    ) {
        return new AuthenticateAccountUseCase(
                tokenService,
                authService,
                accountRepository,
                userReadRepository,
                domainEventBus
        );
    }

    @Bean
    public TerminateSessionUseCase accountLogoutUseCase(
            TokenService tokenService,
            AccountRepository accountRepository,
            DomainEventBus domainEventBus
    ) {
        return new TerminateSessionUseCase(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }

    @Bean
    public RefreshSessionUseCase refreshSessionUseCase(
            TokenService tokenService,
            AccountRepository accountRepository
    ) {
        return new RefreshSessionUseCase(
                tokenService,
                accountRepository
        );
    }
}
