package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.FriendShipId;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.mapping.FriendMapper;
import toast.model.entities.users.FriendEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendShipRepositoryMySQL implements FriendShipRepository {

    private final JpaFriendShipRepository jpaFriendShipRepository;

    @Override
    public void save(FriendShip friendship) {
        FriendEntity friendEntity = FriendMapper.toEntity(friendship);
        jpaFriendShipRepository.save(friendEntity);
    }

    @Override
    public Optional<FriendShip> findByUsersIds(UserId userId, UserId friendId) {
        return jpaFriendShipRepository.findByUserUserIdAndFriendUserId(userId.getValue(), friendId.getValue())
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
