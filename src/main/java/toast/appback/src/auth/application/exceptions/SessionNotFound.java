package toast.appback.src.auth.application.exceptions;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.application.ApplicationException;

public class SessionNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "session with id %s for account with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "the session for the account was not found.";
    private final SessionId sessionId;
    private final AccountId accountId;

    public SessionNotFound(SessionId sessionId, AccountId accountId) {
        super(String.format(MESSAGE_TEMPLATE, sessionId, accountId), FRIENDLY_MESSAGE);
        this.sessionId = sessionId;
        this.accountId = accountId;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public AccountId getAccountId() {
        return accountId;
    }
}
