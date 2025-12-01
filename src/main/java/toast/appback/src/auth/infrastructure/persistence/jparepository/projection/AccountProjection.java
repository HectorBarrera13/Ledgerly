package toast.appback.src.auth.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface AccountProjection {
    UUID getAccountId();
    String getEmail();
}
