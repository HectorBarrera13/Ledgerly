package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.appback.src.users.infrastructure.persistence.mapping.FriendMapper;
import toast.model.entities.users.FriendShipEntity;
import toast.model.entities.users.UserEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendShipRepositoryMySQL implements FriendShipRepository {

    private final JpaFriendShipRepository jpaFriendShipRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void save(FriendShip friendship) {
        // 1. Find existing friendship or create a new one
        FriendShipEntity friendShip = jpaFriendShipRepository
                .findByFirstUser_UserIdAndSecondUser_UserId(
                        friendship.getFirstUser().getValue(),
                        friendship.getSecondUser().getValue()
                )
                .orElseGet(FriendShipEntity::new);

        // 2. Get valid references
        UserEntity firstRef = jpaUserRepository.getReferenceByUserId(friendship.getFirstUser().getValue());
        UserEntity secondRef = jpaUserRepository.getReferenceByUserId(friendship.getSecondUser().getValue());

        // 3. Set values
        friendShip.setFirstUser(firstRef);
        friendShip.setSecondUser(secondRef);
        friendShip.setCreatedAt(friendship.getCreatedAt());

        // 4. Save the entity
        jpaFriendShipRepository.save(friendShip);
    }

    @Override
    public Optional<FriendShip> findByUsersIds(UserId userId, UserId friendId) {
        return jpaFriendShipRepository.findByFirstUser_UserIdAndSecondUser_UserId(userId.getValue(), friendId.getValue())
                .map(FriendMapper::toDomain);
    }

    @Override
    public Optional<FriendShip> findById(Long id) {
        return jpaFriendShipRepository.findById(id)
                .map(FriendMapper::toDomain);
    }

    @Override
    public void delete(FriendShip friendship) {
        jpaFriendShipRepository.deleteByUserIdAndFriendId(
                friendship.getFirstUser().getValue(),
                friendship.getSecondUser().getValue()
        );
    }
}
