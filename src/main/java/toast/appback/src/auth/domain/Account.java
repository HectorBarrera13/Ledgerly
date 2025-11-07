package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.event.AllSessionsRevoked;
import toast.appback.src.auth.domain.event.SessionAdded;
import toast.appback.src.auth.domain.event.SessionRevoked;
import toast.appback.src.shared.DomainEvent;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.domain.UserId;

import java.util.*;

public class Account {
    private static final int MAX_SESSIONS = 5;
    private final AccountId accountId;
    private final UserId userId;
    private Email email;
    private Password password;
    private List<Session> sessions = new ArrayList<>();
    private List<DomainEvent> domainEvents = new ArrayList<>();

    public Account(AccountId accountId, UserId userId, Email email, Password password) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public Account(AccountId accountId, UserId userId, Email email, Password password, List<Session> sessions, List<DomainEvent> domainEvents) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.sessions = new ArrayList<>(sessions);
        this.domainEvents = new ArrayList<>(domainEvents);
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

    public Result<Void, DomainError> addSession(Session session) {
        long notRevokedSessions = this.sessions.stream()
                .filter(Session::isValid)
                .count();
        if (notRevokedSessions >= MAX_SESSIONS) {
            return Result.failure(DomainError.businessRule("session limit exceeded")
                    .withDetails("Account " + accountId + " has reached the maximum number of sessions: " + MAX_SESSIONS));
        }
        this.sessions.add(session);
        this.addDomainEvent(new SessionAdded(this.accountId, session.sessionId()));
        return Result.success();
    }

    public Result<Void, DomainError> revokeSession(SessionId sessionId) {
        boolean removed = this.sessions.stream()
                .filter(s -> s.sessionId().equals(sessionId))
                .findFirst()
                .map(s -> {
                    s.revoke();
                    return true;
                }).orElse(false);
        if (!removed) {
            return Result.failure(DomainError.unexpected("session error",
                    "Session with ID: " + sessionId + " could not be revoked from account " + accountId));
        }
        this.addDomainEvent(new SessionRevoked(this.accountId, sessionId));
        return Result.success();
    }

    public boolean hasActiveSession(SessionId sessionId) {
        return this.sessions.stream()
                .anyMatch(s -> s.sessionId().equals(sessionId) && s.isValid());
    }

    public void revokeAllSessions() {
        this.sessions.clear();
        this.addDomainEvent(new AllSessionsRevoked(this.accountId));
    }

    public void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
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
                ", domainEvents=" + domainEvents +
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
