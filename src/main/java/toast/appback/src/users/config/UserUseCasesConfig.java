package toast.appback.src.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.usecase.implementation.AddFriendUseCase;
import toast.appback.src.users.application.usecase.implementation.CreateUserUseCase;
import toast.appback.src.users.application.usecase.implementation.RemoveFriendUseCase;
import toast.appback.src.users.domain.DefaultUser;
import toast.appback.src.users.domain.UserFactory;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

@Configuration
public class UserUseCasesConfig {

    @Bean
    public DefaultUser defaultUser() {
        return new DefaultUser();
    }

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            UserFactory userFactory,
            EventBus eventBus
    ) {
        return new CreateUserUseCase(
                userRepository,
                userFactory,
                eventBus
        );
    }

    @Bean
    public AddFriendUseCase addFriendUseCase(
            FriendShipRepository friendShipRepository,
            UserRepository userRepository,
            EventBus eventBus
    ) {
        return new AddFriendUseCase(
                friendShipRepository,
                userRepository,
                eventBus
        );
    }

    @Bean
    public RemoveFriendUseCase removeFriendUseCase(
            FriendShipRepository friendShipRepository,
            EventBus eventBus
    ) {
        return new RemoveFriendUseCase(
                friendShipRepository,
                eventBus
        );
    }
}
