package toast.appback.src.auth.domain;

import java.time.Instant;
import java.util.Objects;

public class Session {

    private final SessionId sessionId;
    private SessionStatus status;
    private final Instant expiration;
    private static final long MAX_DURATION_SECONDS = 60 * 60 * 24 * 20; // 20 days

    public Session(SessionId sessionId, SessionStatus status, Instant expiration) {
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

    public long getMaxDurationSeconds() {
        return MAX_DURATION_SECONDS;
    }

    public static Session create() {
        return new Session(SessionId.generate(), SessionStatus.NORMAL, Instant.now().plusSeconds(MAX_DURATION_SECONDS));
    }

    public static Session load(SessionId sessionId, SessionStatus status, Instant expiration) {
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

    @Override
    public String toString() {
        return "Session{" +
                "session=" + sessionId +
                ", status=" + status +
                ", expiration=" + expiration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Session session)) return false;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId);
    }
}
