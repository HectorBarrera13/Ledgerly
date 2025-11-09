package toast.appback.src.users.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.users.FriendEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaFriendShipRepository extends JpaRepository<FriendEntity, Long> {
    Optional<FriendEntity> findByUserUserIdAndFriendUserId(UUID userUserId, UUID friendUserId);
}
