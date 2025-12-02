package toast.appback.src.groups.infrastructure.persistence.jparepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.groups.infrastructure.persistence.jparepository.projection.GroupProjection;
import toast.model.entities.group.GroupEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaGroupRepository extends JpaRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByGroupId(UUID groupId);

    @Query("""
        SELECT g
        FROM GroupEntity g
        WHERE g.groupId = :groupId
    """)
    Optional<GroupProjection> findGroupProjectionByGroupId(UUID groupId);

    @Query("""
        SELECT g
        FROM MemberEntity m
        JOIN m.group g
        WHERE m.user.userId = :userId
    """)
    Page<GroupProjection> findAllByUserId(UUID userId, Pageable pageable);

    @Query("""
           SELECT
                g.groupId AS groupId,
                g.name AS name,
                g.description AS description,
                g.createdAt AS createdAt
           FROM GroupEntity g
           JOIN MemberEntity m ON m.group.groupId = g.groupId
           WHERE g.groupId = :groupId AND m.user.userId = :userId
           """)
    Optional<GroupProjection> findGroupByIdAndUser(
            @Param("groupId") UUID groupId,
            @Param("userId") UUID userId
    );
}
