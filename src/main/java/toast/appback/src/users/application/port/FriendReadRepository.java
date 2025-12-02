package toast.appback.src.users.application.port;

import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public interface FriendReadRepository {
    PageResult<FriendView, UUID> findFriendsByUserId(UserId userId, PageRequest pageRequest);

    PageResult<FriendView, UUID> findFriendsByUserIdAfterCursor(UserId userId, CursorRequest<UUID> cursorRequest);

    PageResult<FriendView, UUID> searchFriendsByName(UserId userId, String nameQuery, PageRequest pageRequest);

    PageResult<FriendView, UUID> searchFriendsByPhone(UserId userId, String phoneQuery, PageRequest pageRequest);
}
