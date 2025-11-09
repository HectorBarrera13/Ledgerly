package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.FriendShipId;
import toast.model.entities.users.FriendEntity;

public class FriendMapper {
    public static FriendEntity toEntity(FriendShip friendship) {
        FriendEntity entity = new FriendEntity();
        entity.setId(friendship.getFriendshipId().id());
        entity.setUser(UserMapper.toEntity(friendship.getRequest()));
        entity.setFriend(UserMapper.toEntity(friendship.getReceiver()));
        entity.setAddedAt(friendship.getAddTime());
        return entity;
    }

    public static FriendShip toDomain(FriendEntity entity) {
        return new FriendShip(
                new FriendShipId(entity.getId()),
                UserMapper.toDomain(entity.getUser()),
                UserMapper.toDomain(entity.getUser()),
                entity.getAddedAt()
        );
    }
}
