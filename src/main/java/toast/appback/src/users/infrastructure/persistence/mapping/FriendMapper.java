package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.FriendShipId;
import toast.model.entities.users.FriendEntity;
import toast.model.entities.users.UserEntity;

public class FriendMapper {
    public static FriendEntity toEntity(FriendShip friendship, FriendEntity existingEntity) {
        existingEntity.setId(friendship.getFriendshipId().getValue());
        existingEntity.setUser(UserMapper.toEntity(friendship.getRequest(), existingEntity.getUser()));
        existingEntity.setFriend(UserMapper.toEntity(friendship.getReceiver(), existingEntity.getFriend()));
        existingEntity.setAddedAt(friendship.getAddTime());
        return existingEntity;
    }

    public static FriendShip toDomain(FriendEntity entity) {
        return new FriendShip(
                FriendShipId.load(entity.getId()),
                UserMapper.toDomain(entity.getUser()),
                UserMapper.toDomain(entity.getUser()),
                entity.getAddedAt()
        );
    }
}
