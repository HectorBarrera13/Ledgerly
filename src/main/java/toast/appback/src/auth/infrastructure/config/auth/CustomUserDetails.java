package toast.appback.src.auth.infrastructure.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final AccountId id;

    private final String email;

    private final String hashedPassword;

    private final Collection<? extends GrantedAuthority> authorities;

    private SessionId sessionId;

    public CustomUserDetails(AccountId id, String email, String hashedPassword) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.authorities = List.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public AccountId getId() {
        return id;
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
