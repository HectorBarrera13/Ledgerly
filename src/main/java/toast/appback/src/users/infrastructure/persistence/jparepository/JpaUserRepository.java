package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.users.infrastructure.persistence.jparepository.projection.UserProjection;
import toast.model.entities.users.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(UUID userId);

    @Query("""
                SELECT u.userId AS userId,u.firstName AS firstName, u.lastName AS lastName,
                    CONCAT(u.phone.countryCode,'-',u.phone.number) AS phone
                FROM UserEntity u
                WHERE u.userId = :userId
            """)
    Optional<UserProjection> findUserProjectionByUserId(@Param("userId") UUID userId);

    @Query("""
                SELECT u.profilePictureFileName
                FROM UserEntity u
                WHERE u.userId = :userId
            """)
    Optional<String> getProfilePictureFileNameByUserId(UUID userId);
}