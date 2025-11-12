package toast.appback.src.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.implementation.*;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.domain.DefaultAccount;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.application.EventBus;
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
    @Transactional
    public CreateAccount createAccount(
            AccountRepository accountRepository,
            AccountFactory accountFactory
    ) {
        return new CreateAccountUseCase(
                accountRepository,
                accountFactory
        );
    }


    @Bean
    @Transactional
    public RegisterAccountUseCase registerAccountUseCaseV2(
            CreateAccount createAccount,
            CreateUser createUser,
            TokenService tokenService,
            EventBus eventBus
    ) {
        return new RegisterAccountUseCase(
                createUser,
                createAccount,
                tokenService,
                eventBus
        );
    }

    @Bean
    @Transactional
    public AuthenticateAccountUseCase accountLoginUseCase(
            TokenService tokenService,
            AuthService authService,
            AccountRepository accountRepository,
            EventBus eventBus
    ) {
        return new AuthenticateAccountUseCase(
                tokenService,
                authService,
                accountRepository,
                eventBus
        );
    }

    @Bean
    @Transactional
    public TerminateSessionUseCase accountLogoutUseCase(
            TokenService tokenService,
            AccountRepository accountRepository,
            EventBus eventBus
    ) {
        return new TerminateSessionUseCase(
                tokenService,
                accountRepository,
                eventBus
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
