package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.users.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(UUID userId);
}