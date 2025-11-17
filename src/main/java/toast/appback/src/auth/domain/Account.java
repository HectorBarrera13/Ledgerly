package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.event.AllSessionsRevoked;
import toast.appback.src.auth.domain.event.SessionAdded;
import toast.appback.src.auth.domain.event.SessionRevoked;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.*;

public class Account {
    private static final int MAX_SESSIONS = 5;
    private final AccountId accountId;
    private final UserId userId;
    private final Email email;
    private final Password password;
    private final List<Session> sessions; // Only five active sessions allowed
    private final Instant createdAt;
    private final List<DomainEvent> userEvents = new ArrayList<>();

    public Account(AccountId accountId, UserId userId, Email email, Password password, Instant createdAt, List<Session> sessions) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.sessions = new ArrayList<>(sessions);
    }

    public static Account create(UserId userId, Email email, Password password) {
        Account account = new Account(AccountId.generate(), userId, email, password, Instant.now(), List.of());
        account.recordEvent(new AccountCreated(account.getAccountId(), userId, email));
        return account;
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

    public Instant getCreatedAt() {
        return createdAt;
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
                    .withDetails("account " + accountId +
                            " has reached the maximum number of sessions: " + MAX_SESSIONS));
        }
        Session newSession = Session.create();
        this.sessions.add(newSession);
        this.recordEvent(new SessionAdded(this.accountId, newSession.getSessionId()));
        return Result.ok(newSession);
    }

    public Result<Void, DomainError> revokeSession(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.businessRule("session not found")
                    .withDetails(String.format("session with id %s not found", sessionId))
                    .withBusinessCode(AccountBusinessCode.SESSION_NOT_FOUND));
        }
        Session session = foundSession.get();
        if (session.isRevoked()) {
            return Result.failure(DomainError.businessRule("session already revoked")
                    .withBusinessCode(AccountBusinessCode.SESSION_ALREADY_REVOKED)
                    .withDetails("session with id: " + sessionId + " is already revoked for account " + accountId));
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

    public Result<Void, DomainError> verifySession(SessionId sessionId) {
        Optional<Session> foundSession = this.findSession(sessionId);
        if (foundSession.isEmpty()) {
            return Result.failure(DomainError.businessRule("session not found")
                    .withDetails("session with id: " + sessionId + " not found for account " + accountId)
                    .withBusinessCode(AccountBusinessCode.SESSION_NOT_FOUND)
            );
        }
        if (foundSession.get().isRevoked()) {
            return Result.failure(DomainError.businessRule("session revoked")
                    .withDetails("session with id: " + sessionId + " is revoked for account " + accountId)
                    .withBusinessCode(AccountBusinessCode.SESSION_REVOKED)
            );
        }
        return Result.ok();
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
