package toast.appback.src.users.application.port;

import toast.appback.src.users.application.communication.result.UserQueryResult;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

public interface UserQueryRepository {
    Optional<UserQueryResult> getUser(UserId userId);
}
