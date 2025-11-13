package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.FriendShipId;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.appback.src.users.infrastructure.persistence.mapping.FriendMapper;
import toast.model.entities.users.FriendEntity;
import toast.model.entities.users.UserEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendShipRepositoryMySQL implements FriendShipRepository {

    private final JpaFriendShipRepository jpaFriendShipRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void save(FriendShip friendship) {
        Optional<FriendEntity> existingEntityOpt = jpaFriendShipRepository
                .findByUserIdAndFriendId(
                        friendship.getRequest().getUserId().getValue(),
                        friendship.getReceiver().getUserId().getValue()
                );

        if (existingEntityOpt.isPresent()) {
            updateExistingFriendship(friendship, existingEntityOpt.get());
        } else {
            saveNewFriendship(friendship);
        }
    }

    private void saveNewFriendship(FriendShip friendship) {
        FriendEntity friendEntity = new FriendEntity();
        UserEntity userEntity = jpaUserRepository.findByUserId(friendship.getRequest().getUserId().getValue())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        UserEntity friendUserEntity = jpaUserRepository.findByUserId(friendship.getReceiver().getUserId().getValue())
                .orElseThrow(() -> new IllegalStateException("Friend user not found"));
        friendEntity.setUser(userEntity);
        friendEntity.setFriend(friendUserEntity);
        friendEntity.setAddedAt(friendship.getAddTime());
        jpaFriendShipRepository.save(friendEntity);
    }

    private void updateExistingFriendship(FriendShip friendship, FriendEntity existingEntity) {
        FriendEntity updatedEntity = FriendMapper.toEntity(friendship, existingEntity);
        jpaFriendShipRepository.save(updatedEntity);
    }

    @Override
    public Optional<FriendShip> findByUsersIds(UserId userId, UserId friendId) {
        return jpaFriendShipRepository.findByUserIdAndFriendId(userId.getValue(), friendId.getValue())
                .map(FriendMapper::toDomain);
    }

    @Override
    public Optional<FriendShip> findById(Long id) {
        return jpaFriendShipRepository.findById(id)
                .map(FriendMapper::toDomain);
    }

    @Override
    public void delete(FriendShipId friendshipId) {
        jpaFriendShipRepository.deleteById(friendshipId.getValue());
    }
}
