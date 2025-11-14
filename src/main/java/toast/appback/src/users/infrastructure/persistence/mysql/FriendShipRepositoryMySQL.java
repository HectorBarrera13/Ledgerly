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
import toast.model.entities.users.FriendShipId;

@Repository
@RequiredArgsConstructor
public class FriendShipRepositoryMySQL implements FriendShipRepository {

    private final JpaFriendShipRepository jpaFriendShipRepository;
    private final JpaUserRepository jpaUserRepository;
    private final FriendMapper friendMapper;

    @Override
    public void save(FriendShip friendship) {
        FriendShipEntity friendshipEntity = friendMapper.toEntity(friendship);
        jpaFriendShipRepository.save(friendshipEntity);
    }

    @Override
    public boolean existsFriendShip(UserId userId, UserId friendId) {
        Long userIdA = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        Long userIdB = jpaUserRepository.findByUserId(friendId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        FriendShipId friendShipId = friendMapper.normalizeAndGetId(userIdA, userIdB);
        return jpaFriendShipRepository.existsById(friendShipId);
    }


    @Override
    public void delete(UserId userIdA, UserId userIdB) {
        Long idA = jpaUserRepository.findByUserId(userIdA.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        Long idB = jpaUserRepository.findByUserId(userIdB.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        FriendShipId friendShipId = friendMapper.normalizeAndGetId(idA, idB);
        jpaFriendShipRepository.deleteById(friendShipId);
    }
}
