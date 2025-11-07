package toast.appback.src.auth.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        Result<Void, AppError> validationResult = tokenService.verifyToken(token);
        if (validationResult.isFailure()) {
            filterChain.doFilter(request, response);
            return;
        }

        Result<AccountInfo, AppError> claimsResult = tokenService.extractClaims(token);
        if (claimsResult.isFailure()) {
            filterChain.doFilter(request, response);
            return;
        }

        AccountInfo accountInfo = claimsResult.getValue();

        SessionId sessionId = accountInfo.sessionId();
        String email = accountInfo.email();
        AccountId accountId = accountInfo.accountId();


        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = new CustomUserDetails(
                    accountId,
                    email,
                    ""
            );
            userDetails.setSessionId(sessionId);
            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
