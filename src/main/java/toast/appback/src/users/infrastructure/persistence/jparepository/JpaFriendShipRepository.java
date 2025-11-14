package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.users.infrastructure.persistence.jparepository.projection.FriendProjection;
import toast.model.entities.users.FriendShipEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFriendShipRepository extends JpaRepository<FriendShipEntity, Long> {

    Optional<FriendShipEntity> findByFirstUser_UserIdAndSecondUser_UserId(UUID firstUserUserId, UUID secondUserUserId);

    @Modifying
    @Query("DELETE FROM FriendShipEntity f WHERE f.firstUser.userId = :userId AND f.secondUser.userId = :friendId")
    void deleteByUserIdAndFriendId(UUID userId, UUID friendId);

    @Query(value = """
            SELECT BIN_TO_UUID(u.user_id) AS userId,
                   u.first_name AS firstName,
                   u.last_name AS lastName,
                   CONCAT(u.country_code,'-',u.number) AS phone,
                   f.created_at AS addedAt
            FROM friend_ship f
            JOIN user u ON f.second_user_id = u.id
            WHERE f.first_user_id = (
                      SELECT u0.id FROM user u0 WHERE u0.user_id = :userId
                  )
            ORDER BY f.second_user_id
            LIMIT :limit
    """, nativeQuery = true)
    List<FriendProjection> findAllUserFriendsByUserId(@Param("userId") UUID userId, @Param("limit") int limit);

    @Query(value = """
        SELECT BIN_TO_UUID(u.user_id) AS userId,
               u.first_name AS firstName,
               u.last_name AS lastName,
               CONCAT(u.country_code,'-',u.number) AS phone,
               f.created_at AS addedAt
        FROM friend_ship f
        JOIN user u ON f.second_user_id = u.id
        WHERE f.first_user_id = (
                  SELECT u0.id FROM user u0 WHERE u0.user_id = :userId
              )
          AND f.second_user_id > (
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
