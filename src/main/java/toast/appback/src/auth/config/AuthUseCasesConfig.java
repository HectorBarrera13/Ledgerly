package toast.appback.src.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.AccountLoginUseCase;
import toast.appback.src.auth.application.usecase.implementation.AccountLogoutUseCase;
import toast.appback.src.auth.application.usecase.implementation.RefreshSessionUseCase;
import toast.appback.src.auth.application.usecase.implementation.RegisterAccountUseCase;
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
    public RegisterAccountUseCase registerAccountUseCase(
            AccountRepository accountRepository,
            AccountFactory accountFactory,
            TokenService tokenService,
            CreateUser createUserUseCase,
            EventBus eventBus
    ) {
        return new RegisterAccountUseCase(
                accountRepository,
                accountFactory,
                tokenService,
                createUserUseCase,
                eventBus
        );
    }

    @Bean
    @Transactional
    public AccountLoginUseCase accountLoginUseCase(
            TokenService tokenService,
            AuthService authService,
            AccountRepository accountRepository,
            EventBus eventBus
    ) {
        return new AccountLoginUseCase(
                tokenService,
                authService,
                accountRepository,
                eventBus
        );
    }

    @Bean
    @Transactional
    public AccountLogoutUseCase accountLogoutUseCase(
            TokenService tokenService,
            AuthService authService,
            EventBus eventBus
    ) {
        return new AccountLogoutUseCase(
                tokenService,
                authService,
                eventBus
        );
    }

    @Bean
    public RefreshSessionUseCase refreshSessionUseCase(
            TokenService tokenService,
            AccountRepository accountRepository,
            EventBus eventBus
    ) {
        return new RefreshSessionUseCase(
                tokenService,
                accountRepository,
                eventBus
        );
    }
}
