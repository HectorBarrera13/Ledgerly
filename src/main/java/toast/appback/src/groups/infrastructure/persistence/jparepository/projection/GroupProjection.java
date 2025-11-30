package toast.appback.src.groups.infrastructure.persistence.jparepository.projection;

import java.time.Instant;
import java.util.UUID;

public interface GroupProjection {
    UUID getGroupId();
    String getName();
    String getDescription();
    Instant getCreatedAt();
}
