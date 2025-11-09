package toast.appback.src.auth.domain;

import java.time.Instant;

public class Session {

    private final SessionId sessionId;
    private SessionStatus status;
    private final Instant expiration;
    private static final long MAX_DURATION_SECONDS = 60 * 60 * 24 * 20; // 20 days

    private Session(SessionId sessionId, SessionStatus status, Instant expiration) {
        this.sessionId = sessionId;
        this.status = status;
        this.expiration = expiration;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public static Session create(SessionId sessionId) {
        return new Session(sessionId, SessionStatus.NORMAL, Instant.now().plusSeconds(MAX_DURATION_SECONDS));
    }

    public static Session load(SessionId sessionId, Instant expiration, SessionStatus status) {
        return new Session(sessionId, status, expiration);
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
