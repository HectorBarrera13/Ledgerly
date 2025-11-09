package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.FriendShipId;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

public interface FriendShipRepository {
    void save(FriendShip friendship);
    Optional<FriendShip> findByUsersIds(UserId userId, UserId friendId);
    Optional<FriendShip> findById(Long id);
    void delete(FriendShipId friendshipId);
}
