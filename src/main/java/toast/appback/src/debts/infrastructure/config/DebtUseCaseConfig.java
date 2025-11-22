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
    public AcceptDebtUseCase acceptDebtUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new AcceptDebtUseCase(
                debtRepository,
                domainEventBus
        );
    }

    @Bean
    public ConfirmDebtPaymentUseCase confirmDebtPaymentUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        return new ConfirmDebtPaymentUseCase(
                debtRepository,
                domainEventBus
        );
    }

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
    public EditDebtUseCase editDebtBetweenUsersUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new EditDebtUseCase(
                debtRepository,
                domainEventBus
        );
    }

    @Bean
    public RejectDebtUseCase rejectDebtUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new RejectDebtUseCase(
                debtRepository,
                domainEventBus
        );
    }

    @Bean("rejectDebtPaymentUseCase")
    public RejectDebtPaymentUseCase rejectDebtPaymentUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new RejectDebtPaymentUseCase(
                debtRepository,
                domainEventBus
        );
    }

    @Bean
    public ReportDebtPaymentUseCase reportDebtPaymentUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        return new ReportDebtPaymentUseCase(
                debtRepository,
                domainEventBus
        );
    }

}
