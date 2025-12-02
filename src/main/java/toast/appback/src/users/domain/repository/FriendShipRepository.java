package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;

public interface FriendShipRepository {
    void save(FriendShip friendship);

    boolean existsFriendShip(UserId userIdA, UserId userIdB);

    void delete(UserId userIdA, UserId userIdB);
}
