package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.event.AllSessionsRevoked;
import toast.appback.src.auth.domain.event.SessionAdded;
import toast.appback.src.auth.domain.event.SessionRevoked;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.*;

/**
 * Agregado de dominio que representa la cuenta de autenticación de un usuario.
 *
 * <p>Responsabilidades principales:
 * - Gestionar sesiones activas (límite de sesiones simultáneas).
 * - Iniciar y revocar sesiones, validando reglas de negocio.
 * - Registrar eventos de dominio relacionados con la cuenta.
 * <p>
 * Notas:
 * - El límite máximo de sesiones activas está definido por {@code MAX_SESSIONS}.
 */
public class Account {
    private static final int MAX_SESSIONS = 10;
    private static final String SESSION_MESSAGE = "session with id: ";
    private final AccountId accountId;
    private final UserId userId;
    private final Email email;
    private final Password password;
    private final List<Session> sessions; // Only ten active sessions allowed
    private final Instant createdAt;
    private final List<DomainEvent> userEvents = new ArrayList<>();

    private Account(AccountId accountId, UserId userId, Email email, Password password, Instant createdAt, List<Session> sessions) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.sessions = new ArrayList<>(sessions);
    }

    /**
     * Crea una nueva cuenta para un usuario dado y registra el evento {@link AccountCreated}.
     *
     * @param userId   Identificador del usuario asociado.
     * @param email    Email validado.
     * @param password Contraseña hasheada/valida.
     * @return Nueva instancia de {@link Account} creada.
     */
    public static Account create(UserId userId, Email email, Password password) {
        Account account = new Account(AccountId.generate(), userId, email, password, Instant.now(), List.of());
        account.recordEvent(new AccountCreated(account.getAccountId(), userId, email));
        return account;
    }

    /**
     * Carga una cuenta existente con sus sesiones (sin registrar eventos).
     *
     * @param accountId Identificador de la cuenta.
     * @param userId    Identificador del usuario asociado.
     * @param email     Email asociado.
     * @param password  Contraseña almacenada.
     * @param createdAt Fecha de creación.
     * @param sessions  Lista de sesiones existentes.
     * @return Instancia de {@link Account} reconstruida.
     */
    public static Account load(AccountId accountId, UserId userId, Email email, Password password, Instant createdAt, List<Session> sessions) {
        return new Account(accountId, userId, email, password, createdAt, sessions);
    }

    /**
     * @return Identificador de la cuenta.
     */
    public AccountId getAccountId() {
        return accountId;
    }

    /**
     * @return Identificador del usuario asociado a la cuenta.
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * @return Email asociado a la cuenta.
     */
    public Email getEmail() {
        return email;
    }

    /**
     * @return Fecha de creación de la cuenta.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @return Contraseña (hashed) almacenada para la cuenta.
     */
    public Password getPassword() {
        return password;
    }

    /**
     * @return Lista inmutable de sesiones asociadas a la cuenta.
     */
    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    /**
     * Inicia una nueva sesión si no se supera el límite de sesiones activas.
     *
     * @return Resultado con la nueva {@link Session} o un {@link DomainError} cuando la regla de negocio falla.
     */
    public Result<Session, DomainError> startSession() {
        long notRevokedSessions = this.sessions.stream()
                .filter(Session::isValid)
                .count();
        if (notRevokedSessions >= MAX_SESSIONS) {
            return Result.failure(DomainError
                    .businessRule("session limit exceeded")
                    .withBusinessCode(AccountBusinessCode.SESSION_LIMIT_EXCEEDED)
                    .withDetails("account " + accountId +
                            " has reached the maximum number of sessions: " + MAX_SESSIONS));
        }
        Session newSession = Session.create();
        this.sessions.add(newSession);
        this.recordEvent(
                new SessionAdded(this.accountId, newSession.getSessionId())
        );
        return Result.ok(newSession);
    }

    /**
     * Revoca una sesión existente por su identificador.
     *
     * @param sessionId Identificador de la sesión a revocar.
     * @return Resultado vacío si tiene éxito o un {@link DomainError} en caso de fallo.
     */
    public Result<Void, DomainError> revokeSession(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.businessRule("session not found")
                    .withDetails(String.format("%s%s not found for account %s", SESSION_MESSAGE, sessionId, accountId))
                    .withBusinessCode(AccountBusinessCode.SESSION_NOT_FOUND));
        }
        Session session = foundSession.get();
        if (session.isRevoked()) {
            return Result.failure(DomainError.businessRule("session already revoked")
                    .withBusinessCode(AccountBusinessCode.SESSION_ALREADY_REVOKED)
                    .withDetails(String.format("%s%s is already revoked for account %s", SESSION_MESSAGE, sessionId, accountId)));
        }
        session.revoke();
        this.recordEvent(new SessionRevoked(this.accountId, sessionId));
        return Result.ok();
    }

    private Optional<Session> findSession(SessionId sessionId) {
        return this.sessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();
    }

    /**
     * Verifica que una sesión exista y no esté revocada.
     *
     * @param sessionId Identificador de la sesión a verificar.
     * @return Resultado vacío si la sesión es válida, o un {@link DomainError} si no existe o está revocada.
     */
    public Result<Void, DomainError> verifySession(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.businessRule("session not found")
                    .withDetails(String.format("%s%s not found for account %s", SESSION_MESSAGE, sessionId, accountId))
                    .withBusinessCode(AccountBusinessCode.SESSION_NOT_FOUND)
            );
        }
        if (foundSession.get().isRevoked()) {
            return Result.failure(DomainError.businessRule("session revoked")
                    .withDetails(String.format("%s%s is revoked for account %s", SESSION_MESSAGE, sessionId, accountId))
                    .withBusinessCode(AccountBusinessCode.SESSION_REVOKED)
            );
        }
        return Result.ok();
    }

    /**
     * Revoca todas las sesiones activas de la cuenta y registra el evento {@link AllSessionsRevoked}.
     */
    public void revokeAllSessions() {
        this.sessions.clear();
        this.recordEvent(new AllSessionsRevoked(this.accountId));
    }

    /**
     * Registra un evento de dominio asociado a la cuenta.
     *
     * @param event Evento a registrar.
     */
    public void recordEvent(DomainEvent event) {
        userEvents.add(event);
    }

    /**
     * Devuelve y limpia los eventos de dominio acumulados por la cuenta (efecto secundario).
     *
     * @return Lista de eventos registrados hasta el momento.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(userEvents);
        userEvents.clear();
        return events;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", email=" + email +
                ", password=" + password +
                ", sessions=" + sessions +
                ", domainEvents=" + userEvents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountId, account.accountId) && Objects.equals(userId, account.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId);
    }
}
