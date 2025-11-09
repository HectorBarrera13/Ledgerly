package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.users.application.communication.result.UserQueryResult;
import toast.appback.src.users.application.port.UserQueryRepository;
import toast.appback.src.users.application.usecase.contract.GetUser;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;
import java.util.UUID;

public class GetUserUseCase implements GetUser {
    private final UserQueryRepository userQueryRepository;

    public GetUserUseCase(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    @Override
    public Optional<UserQueryResult> getUser(UUID userId) {
        return userQueryRepository.getUser(UserId.load(userId));
    }
}
