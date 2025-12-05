package toast.appback.src.auth.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa una sesión de autenticación asociada a una cuenta.
 * <p>
 * Contiene identificador, estado, fecha de creación y expiración. Proporciona
 * utilidades para revocar y comprobar validez.
 */
public class Session {

    private static final long MAX_DURATION_SECONDS = 60 * 60 * 24 * 20L; // 20 days
    private final SessionId sessionId;
    private final Instant expiration;
    private final Instant createdAt;
    private SessionStatus status;

    private Session(SessionId sessionId, SessionStatus status, Instant createdAt, Instant expiration) {
        this.sessionId = sessionId;
        this.status = status;
        this.createdAt = createdAt;
        this.expiration = expiration;
    }

    /**
     * Crea una nueva sesión con duración por defecto.
     *
     * @return Nueva instancia de {@link Session} en estado NORMAL.
     */
    public static Session create() {
        return new Session(SessionId.generate(), SessionStatus.NORMAL, Instant.now(), Instant.now().plusSeconds(MAX_DURATION_SECONDS));
    }

    /**
     * Carga una sesión existente (sin efectos secundarios).
     *
     * @param sessionId  Identificador de la sesión.
     * @param status     Estado de la sesión.
     * @param createdAt  Fecha de creación.
     * @param expiration Fecha de expiración.
     * @return Instancia de {@link Session} reconstruida.
     */
    public static Session load(SessionId sessionId, SessionStatus status, Instant createdAt, Instant expiration) {
        return new Session(sessionId, status, createdAt, expiration);
    }

    /**
     * @return Identificador de la sesión.
     */
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * @return Estado actual de la sesión.
     */
    public SessionStatus getStatus() {
        return status;
    }

    /**
     * @return Fecha de expiración de la sesión.
     */
    public Instant getExpiration() {
        return expiration;
    }

    /**
     * @return Fecha de creación de la sesión.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Revoca la sesión (pasa a estado REVOKED).
     */
    public void revoke() {
        this.status = SessionStatus.REVOKED;
    }

    /**
     * @return true si la sesión está en estado NORMAL (válida).
     */
    public boolean isValid() {
        return this.status == SessionStatus.NORMAL;
    }

    /**
     * @return true si la sesión está revocada.
     */
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
