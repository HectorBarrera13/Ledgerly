package toast.appback.src.auth.infrastructure.persistence.mapping;

import toast.appback.src.auth.domain.Token;
import toast.model.entities.account.TokenEntity;

public class TokenMapper {
    public static TokenEntity toEntity(Token token) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setRefresh(token.value());
        tokenEntity.setType(token.tokenType());
        tokenEntity.setExpiresAt(token.expiresAt());
        return tokenEntity;
    }

    public static Token toDomain(TokenEntity tokenEntity) {
        return new Token(tokenEntity.getRefresh(), tokenEntity.getType(), tokenEntity.getExpiresAt());
    }
}
