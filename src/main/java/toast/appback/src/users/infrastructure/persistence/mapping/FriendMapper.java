package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.users.FriendShipEntity;

public class FriendMapper {

    public static FriendShip toDomain(FriendShipEntity friendShipEntity) {
        return FriendShip.load(
                UserId.load(friendShipEntity.getFirstUser().getUserId()),
                UserId.load(friendShipEntity.getSecondUser().getUserId()),
                friendShipEntity.getCreatedAt()
        );
    }
}
