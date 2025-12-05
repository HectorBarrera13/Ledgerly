package toast.appback.src.groups.infrastructure.persistence.jparepository.projection;

import java.util.UUID;

public interface MemberProjection {
    UUID getUserId();

    UUID getGroupId();

    String getFirstName();

    String getLastName();
}
