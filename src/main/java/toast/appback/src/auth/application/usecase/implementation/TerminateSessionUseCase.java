package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.RevokeSessionException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.DomainEventBus;

/**
 * Implementación del caso de uso que termina (revoca) una sesión a partir de un token.
 *
 * <p>Extrae las claims (sin validación estricta), revoca la sesión indicada y publica eventos.
 */
public class TerminateSessionUseCase implements TerminateSession {
    private final TokenService tokenService;
    private final AccountRepository accountRepository;
    private final DomainEventBus domainEventBus;

    public TerminateSessionUseCase(TokenService tokenService,
                                   AccountRepository accountRepository,
                                   DomainEventBus domainEventBus) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Ejecuta la terminación de sesión.
     *
     * @param accessToken Token (o identificador) que contiene la sesión a terminar.
     * @throws InvalidClaimsException Si las claims no corresponden a una cuenta válida.
     * @throws RevokeSessionException Si la revocación falla por reglas de negocio.
     */
    @Override
    public void execute(String accessToken) {
        TokenClaims tokenClaims = tokenService.extractClaimsFromAccessTokenUnsafe(accessToken);
        Account account = accountRepository.findById(tokenClaims.accountId())
                .orElseThrow(() -> new InvalidClaimsException(String.format("Account with id %s not found", tokenClaims.accountId())));

        account.revokeSession(tokenClaims.sessionId())
                .orElseThrow((RevokeSessionException::new));

        accountRepository.save(account);

        domainEventBus.publishAll(account.pullEvents());
    }
}
