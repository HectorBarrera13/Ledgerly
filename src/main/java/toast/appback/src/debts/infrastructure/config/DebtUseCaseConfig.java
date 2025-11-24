package toast.appback.src.debts.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toast.appback.src.debts.application.usecase.implementation.*;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.domain.repository.UserRepository;

@Configuration
public class DebtUseCaseConfig {

    @Bean
    public CreateDebtBetweenUsersUseCase createDebtBetweenUsersUseCase(
            UserRepository userRepository,
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new CreateDebtBetweenUsersUseCase(
                userRepository,
                debtRepository,
                domainEventBus
        );
    }

    @Bean
    public CreateQuickDebtUseCase createQuickDebtUseCase(
            UserRepository userRepository,
            DebtRepository debtRepository
    ) {
        return new CreateQuickDebtUseCase(
                userRepository,
                debtRepository
        );
    }

    @Bean
    public EditQuickDebtUseCase editDebtBetweenUsersUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus,
            UserRepository userRepository
    ) {
        return new EditQuickDebtUseCase(
                debtRepository,
                domainEventBus,
                userRepository
        );
    }


    @Bean("acceptDebtUseCase")
    public AcceptDebtUseCase acceptDebtUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new AcceptDebtUseCase(
                debtRepository,
                userRepository,
                domainEventBus
        );
    }

    @Bean("declineDebtUseCase")
    public RejectDebtUseCase declineDebtUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new RejectDebtUseCase(
                debtRepository,
                userRepository,
                domainEventBus
        );
    }

    @Bean("reportDebtPaymentUseCase")
    public ReportDebtPaymentUseCase reportDebtPaymentUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new ReportDebtPaymentUseCase(
                debtRepository,
                userRepository,
                domainEventBus
        );
    }

    @Bean("rejectDebtPaymentUseCase")
    public RejectDebtPaymentUseCase rejectDebtPaymentUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new RejectDebtPaymentUseCase(
                debtRepository,
                userRepository,
                domainEventBus
        );
    }

    @Bean("confirmDebtPaymentUseCase")
    public ConfirmDebtPaymentUseCase confirmDebtPaymentUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new ConfirmDebtPaymentUseCase(
                debtRepository,
                userRepository,
                domainEventBus
        );
    }

}
