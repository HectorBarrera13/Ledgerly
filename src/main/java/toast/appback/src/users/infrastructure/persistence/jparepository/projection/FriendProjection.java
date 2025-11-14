package toast.appback.src.users.infrastructure.persistence.jparepository.projection;

import java.time.Instant;
import java.util.UUID;

public interface FriendProjection {
    UUID getUserId();
    String getFirstName();
    String getLastName();
    String getPhone();
    Instant getAddedAt();
}
