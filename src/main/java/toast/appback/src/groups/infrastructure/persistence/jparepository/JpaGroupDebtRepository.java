package toast.appback.src.groups.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.group.GroupDebtEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaGroupDebtRepository extends JpaRepository<GroupDebtEntity, UUID> {

    @Override
    Optional<GroupDebtEntity> findById(UUID id);


}
