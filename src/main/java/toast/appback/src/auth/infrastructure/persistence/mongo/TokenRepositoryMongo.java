package toast.appback.src.auth.infrastructure.persistence.mongo;

import org.springframework.stereotype.Repository;
import toast.appback.src.auth.infrastructure.persistence.jparepository.TokenRepository;
import toast.model.entities.account.TokenEntity;

import java.util.List;
import java.util.UUID;

@Repository
public class TokenRepositoryMongo implements TokenRepository {

    @Override
    public List<TokenEntity> findBySessionIds(List<UUID> sessionIds) {
        return List.of();
    }
}
