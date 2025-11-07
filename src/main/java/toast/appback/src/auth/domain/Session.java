package toast.appback.src.auth.domain;

import java.time.Instant;

public class Session {

    private final SessionId sessionId;
    private final Token token;
    private SessionStatus status;

    private Session(SessionId sessionId, Token token, SessionStatus status) {
        this.sessionId = sessionId;
        this.token = token;
        this.status = status;
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public Token token() {
        return token;
    }

    public SessionStatus status() {
        return status;
    }

    public static Session create(SessionId sessionId, String token, String tokenType, Instant expiration) {
        return new Session(sessionId, Token.create(token, tokenType, expiration), SessionStatus.NORMAL);
    }

    public static Session load(SessionId sessionId, String token, String tokenType, Instant expiration, SessionStatus status) {
        return new Session(sessionId, Token.create(token, tokenType, expiration), status);
    }

    public void revoke() {
        this.status = SessionStatus.REVOKED;
    }

    public boolean isValid() {
        return this.status == SessionStatus.NORMAL;
    }

    public boolean isRevoked() {
        return this.status == SessionStatus.REVOKED;
    }
}
