package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.users.application.communication.result.UserQueryResult;

import java.util.Optional;
import java.util.UUID;

public interface GetUser {
    Optional<UserQueryResult> getUser(UUID userId);
}
