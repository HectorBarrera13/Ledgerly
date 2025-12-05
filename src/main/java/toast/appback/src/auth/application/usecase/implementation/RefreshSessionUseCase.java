package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidSessionException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;

/**
 * Implementación del caso de uso que renueva un access token a partir de un refresh token.
 *
 * <p>Extrae claims, verifica la sesión y genera un nuevo access token.
 */
public class RefreshSessionUseCase implements RefreshSession {
    private final TokenService tokenService;
    private final AccountRepository accountRepository;

    public RefreshSessionUseCase(TokenService tokenService,
                                 AccountRepository accountRepository) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
    }

    /**
     * Ejecuta la renovación del access token.
     *
     * @param refreshToken Token de refresco.
     * @return Nuevo JWT de acceso.
     * @throws InvalidClaimsException  Si las claims no se pueden resolver a una cuenta válida.
     * @throws InvalidSessionException Si la sesión indicada no es válida.
     */
    @Override
    public Jwt execute(String refreshToken) {
        TokenClaims tokenClaims = tokenService.extractClaimsFromRefreshToken(refreshToken);

        Account account = accountRepository.findById(tokenClaims.accountId())
                .orElseThrow(() -> new InvalidClaimsException(String.format("Account with id %s not found", tokenClaims.accountId())));

        SessionId sessionId = tokenClaims.sessionId();

        account.verifySession(sessionId)
                .orElseThrow(InvalidSessionException::new);

        return tokenService.generateAccessToken(
                new TokenClaims(
                        account.getAccountId(),
                        account.getUserId(),
                        sessionId
                )
        );
    }
}
