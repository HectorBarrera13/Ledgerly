package toast.appback.src.auth.application.mother;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

public class TokenMother {
    public static Tokens create() {
        return new Tokens(
                createJwt("access-token-value"),
                createJwt("refresh-token-value")
        );
    }

    public static Jwt createJwt(String value) {
        return new Jwt(
                value,
                java.time.Instant.now().plusSeconds(3600)
        );
    }

    public static TokenClaims createClaims() {
        return new TokenClaims(
                AccountId.generate(),
                UserId.generate(),
                SessionId.generate()
        );
    }

    public static Jwt createJwt() {
        return createJwt("token-value");
    }
}
