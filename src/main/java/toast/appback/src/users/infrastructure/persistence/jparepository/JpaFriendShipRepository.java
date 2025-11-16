package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.users.infrastructure.persistence.jparepository.projection.FriendProjection;
import toast.model.entities.users.FriendShipEntity;
import toast.model.entities.users.FriendShipId;

import java.util.List;

public interface JpaFriendShipRepository extends JpaRepository<FriendShipEntity, FriendShipId> {

    @Query("""
            SELECT u.userId AS userId,
                   u.firstName AS firstName,
                   u.lastName AS lastName,
                   CONCAT(u.phone.countryCode,'-',u.phone.number) AS phone,
                   f.createdAt AS addedAt
            FROM FriendShipEntity f
            JOIN UserEntity u
                ON (u.id = CASE WHEN f.id.userOneId = :me THEN f.id.userTwoId ELSE f.id.userOneId END)
            WHERE f.id.userOneId = :me OR f.id.userTwoId = :me
            ORDER BY u.id DESC
            LIMIT :limit
    """)
    List<FriendProjection> findAllUserFriendsByUserId(@Param("me") Long userId, @Param("limit") int limit);


    @Query("""
            SELECT u.userId AS userId,
                   u.firstName AS firstName,
                   u.lastName AS lastName,
                   CONCAT(u.phone.countryCode,'-',u.phone.number) AS phone,
                   f.createdAt AS addedAt
            FROM FriendShipEntity f
            JOIN UserEntity u
                ON (u.id = CASE WHEN f.id.userOneId = :userId THEN f.id.userTwoId ELSE f.id.userOneId END)
            WHERE (f.id.userOneId = :userId OR f.id.userTwoId = :userId)
              AND u.id < :cursor
            ORDER BY u.id DESC
            LIMIT :limit
    """)
    List<FriendProjection> findAllUserFriendsByUserIdAfterCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );
}
