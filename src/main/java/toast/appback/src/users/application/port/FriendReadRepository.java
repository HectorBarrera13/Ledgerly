package toast.appback.src.users.application.port;

import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public interface FriendReadRepository {
    List<FriendView> findFriendsByUserId(UserId userId, int limit);
    List<FriendView> findFriendsByUserIdAfterCursor(UserId userId, UUID cursor, int limit);
}
