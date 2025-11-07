package toast.appback.src.auth.infrastructure.persistence.jparepository;

import toast.model.entities.account.TokenEntity;

import java.util.List;
import java.util.UUID;

public interface TokenRepository {
    List<TokenEntity> findBySessionIds(List<UUID> sessionIds);
}
