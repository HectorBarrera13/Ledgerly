package toast.appback.src.auth.infrastructure.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private final UUID accountId;
    private final UUID userId;
    private final String hashedPassword;
    private final Collection<? extends GrantedAuthority> authorities;
    private UUID sessionId;

    public CustomUserDetails(AccountId accountId, UserId userId, String hashedPassword) {
        this.accountId = accountId.getValue();
        this.userId = userId.getValue();
        this.hashedPassword = hashedPassword;
        this.authorities = List.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public AccountId getAccountId() {
        return AccountId.load(accountId);
    }

    public UserId getUserId() {
        return UserId.load(userId);
    }

    public SessionId getSessionId() {
        return SessionId.load(sessionId);
    }

    public void setSessionId(SessionId sessionId) {
        this.sessionId = sessionId.getValue();
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return accountId.toString();
    }
}
