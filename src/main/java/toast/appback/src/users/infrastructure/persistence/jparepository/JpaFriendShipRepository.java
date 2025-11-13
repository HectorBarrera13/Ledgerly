package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.users.infrastructure.persistence.jparepository.projection.FriendProjection;
import toast.model.entities.users.FriendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFriendShipRepository extends JpaRepository<FriendEntity, Long> {
    @Query("SELECT f FROM FriendEntity f WHERE f.user.userId = :userId AND f.friend.userId = :friendId")
    Optional<FriendEntity> findByUserIdAndFriendId(@Param("userId") UUID userUserId,@Param("friendId") UUID friendUserId);

    @Query(value = """
            SELECT BIN_TO_UUID(u.user_id) AS userId,
                   u.first_name AS firstName,
                   u.last_name AS lastName,
                   CONCAT(u.country_code,'-',u.number) AS phone,
                   f.added_at AS addedAt
            FROM friend_ship f
            JOIN user u ON f.friend_id = u.id
            WHERE f.user_id = (
                      SELECT u0.id FROM user u0 WHERE u0.user_id = :userId
                  )
            ORDER BY f.friend_id
            LIMIT :limit
    """, nativeQuery = true)
    List<FriendProjection> findAllUserFriendsByUserId(@Param("userId") UUID userId, @Param("limit") int limit);

    @Query(value = """
        SELECT BIN_TO_UUID(u.user_id) AS userId,
               u.first_name AS firstName,
               u.last_name AS lastName,
               CONCAT(u.country_code,'-',u.number) AS phone,
               f.added_at AS addedAt
        FROM friend_ship f
        JOIN user u ON f.friend_id = u.id
        WHERE f.user_id = (
                  SELECT u0.id FROM user u0 WHERE u0.user_id = :userId
              )
          AND f.friend_id > (
                  SELECT u1.id FROM user u1 WHERE u1.user_id = :cursor
              )
        ORDER BY f.friend_id
        LIMIT :limit
    """, nativeQuery = true)
    List<FriendProjection> findAllUserFriendsByUserIdAfterCursor(
            @Param("userId") UUID userId,
            @Param("cursor") UUID cursor,
            @Param("limit") int limit
    );


}
