package toast.appback.src.groups.infrastructure.persistence.jparepository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.groups.infrastructure.persistence.jparepository.projection.MemberProjection;
import org.springframework.data.domain.Pageable;
import toast.model.entities.group.MemberEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMemberRepository extends JpaRepository<MemberEntity, UUID> {

    @Query(
        """
        SELECT m
        FROM MemberEntity m
        WHERE m.group.id = :groupId
            AND m.user.id = :userId
        """
    )
    Optional<MemberEntity> findMemberProjection(@Param("groupId") UUID groupId, @Param("userId") Long userId);

    @Query("""
    SELECT
        u.userId AS userId,
        u.firstName AS firstName,
        u.lastName AS lastName,
        m.addedAt AS addedAt
    FROM MemberEntity m
    JOIN m.user u
    WHERE m.group.groupId = :groupId
    """)
    Page<MemberProjection> findMembersByGroupId(@Param("groupId") UUID groupId, Pageable pageable);

    boolean existsByGroup_GroupIdAndUser_UserId(UUID groupId, UUID userId);

    @Query("""
        SELECT m
        FROM MemberEntity m
        JOIN FETCH m.user u
        WHERE m.group.groupId IN :groupIds
    """)
    List<MemberEntity> findMembersByGroupIds(@Param("groupIds") List<UUID> groupIds);

}
