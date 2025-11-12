package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.event.AllSessionsRevoked;
import toast.appback.src.auth.domain.event.SessionAdded;
import toast.appback.src.auth.domain.event.SessionRevoked;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.UserId;

import java.util.*;

public class Account {
    private static final int MAX_SESSIONS = 5;
    private final AccountId accountId;
    private final UserId userId;
    private final Email email;
    private final Password password;
    private List<Session> sessions = new ArrayList<>(); // Only five active sessions allowed
    private List<DomainEvent> userEvents = new ArrayList<>();

    public Account(AccountId accountId, UserId userId, Email email, Password password) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public Account(AccountId accountId, UserId userId, Email email, Password password, List<Session> sessions, List<DomainEvent> userEvents) {
        this(accountId, userId, email, password);
        this.sessions = new ArrayList<>(sessions);
        this.userEvents = new ArrayList<>(userEvents);
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public UserId getUserId() {
        return userId;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    public Result<Session, DomainError> startSession() {
        long notRevokedSessions = this.sessions.stream()
                .filter(Session::isValid)
                .count();
        if (notRevokedSessions >= MAX_SESSIONS) {
            return Result.failure(DomainError
                    .businessRule("session limit exceeded")
                    .withBusinessCode(AccountBusinessCode.SESSION_LIMIT_EXCEEDED)
                    .withDetails("account " + accountId + " has reached the maximum number of sessions: " + MAX_SESSIONS));
        }
        Session newSession = Session.create();
        this.sessions.add(newSession);
        this.recordEvent(new SessionAdded(this.accountId, newSession.getSessionId()));
        return Result.success(newSession);
    }

    public Result<Void, DomainError> revokeSession(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.integrity("session not found",
                    "session with ID: " + sessionId + " could not be revoked from account " + accountId));
        }
        Session session = foundSession.get();
        if (session.isRevoked()) {
            return Result.failure(DomainError.integrity("session already revoked",
                    "session with ID: " + sessionId + " is already revoked for account " + accountId));
        }
        session.revoke();
        this.recordEvent(new SessionRevoked(this.accountId, sessionId));
        return Result.success();
    }

    public Optional<Session> findSession(SessionId sessionId) {
        return this.sessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();
    }

    public Result<Void, DomainError> verifyValidSessionStatus(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.integrity("session not found",
                    "session with ID: " + sessionId + " not found for account " + accountId));
        }
        Session session = foundSession.get();
        if (!session.isValid()) {
            return Result.failure(DomainError.integrity("inactive session",
                    "session with ID: " + sessionId + " is not active for account " + accountId));
        }
        return Result.success();
    }

    public void revokeAllSessions() {
        this.sessions.clear();
        this.recordEvent(new AllSessionsRevoked(this.accountId));
    }

    public void recordEvent(DomainEvent event) {
        userEvents.add(event);
    }

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
