package toast.appback.src.auth.domain.repository;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;

import java.util.Optional;

/**
 * Repositorio de dominio para persistir y recuperar cuentas.
 */
public interface AccountRepository {
    /**
     * Busca una cuenta por su email.
     *
     * @param email Email asociado a la cuenta.
     * @return Optional con la entidad `Account` si existe.
     */
    Optional<Account> findByEmail(String email);

    /**
     * Busca una cuenta por su identificador.
     *
     * @param accountId Identificador de la cuenta.
     * @return Optional con la entidad `Account` si existe.
     */
    Optional<Account> findById(AccountId accountId);

    /**
     * Persiste o actualiza la entidad `Account`.
     *
     * @param account Cuenta a guardar.
     */
    void save(Account account);
}
