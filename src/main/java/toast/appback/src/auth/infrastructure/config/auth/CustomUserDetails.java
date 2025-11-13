package toast.appback.src.auth.infrastructure.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final AccountId accountId;
    private final UserId userId;
    private final String email;
    private final String hashedPassword;
    private final Collection<? extends GrantedAuthority> authorities;
    private SessionId sessionId;

    public CustomUserDetails(AccountId accountId, UserId userId, String email, String hashedPassword) {
        this.accountId = accountId;
        this.userId = userId;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.authorities = List.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public UserId getUserId() {
        return userId;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public void setSessionId(SessionId sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
