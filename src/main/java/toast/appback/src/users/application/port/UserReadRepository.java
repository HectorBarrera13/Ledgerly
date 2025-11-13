package toast.appback.src.users.application.port;

import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

public interface UserReadRepository {
    Optional<UserView> findById(UserId userId);
    Long count();
}
