package toast.appback.src.users.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface UserProjection {
    UUID getUserId();
    String getFirstName();
    String getLastName();
    String getPhone();
}
