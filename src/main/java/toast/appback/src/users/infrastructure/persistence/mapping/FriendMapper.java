package toast.appback.src.users.infrastructure.persistence.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.model.entities.users.FriendShipEntity;
import toast.model.entities.users.FriendShipId;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FriendMapper {

    private final JpaUserRepository jpaUserRepository;

    public FriendShipEntity toEntity(FriendShip friendship) {
        Long userIdA = jpaUserRepository.findByUserId(friendship.getFirstUser().getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + friendship.getFirstUser().getValue()))
                .getId();
        Long userIdB = jpaUserRepository.findByUserId(friendship.getSecondUser().getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + friendship.getSecondUser().getValue()))
                .getId();
        FriendShipId friendShipId = normalizeAndGetId(userIdA, userIdB);
        return new FriendShipEntity(friendShipId, friendship.getCreatedAt());
    }

    public FriendShipId normalizeAndGetId(Long userIdA, Long userIdB) {
        long firstUser = Math.min(userIdA, userIdB);
        long secondUser = Math.max(userIdA, userIdB);
        return new FriendShipId(firstUser, secondUser);
    }

    public FriendShip toDomain(FriendShipEntity friendship) {
        UUID firstUserId = jpaUserRepository.getReferenceById(friendship.getId().getUserOneId()).getUserId();
        UUID secondUserId = jpaUserRepository.getReferenceById(friendship.getId().getUserTwoId()).getUserId();
        return FriendShip.load(
                UserId.load(firstUserId),
                UserId.load(secondUserId),
                friendship.getCreatedAt()
        );
    }
}
