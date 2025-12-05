package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.domain.AccountId;

import java.util.Optional;

/**
 * Puerto de lectura para obtener vistas de cuentas.
 * <p>
 * Implementaciones deben exponer consultas optimizadas para obtener datos públicos de cuenta.
 */
public interface AccountReadRepository {
    /**
     * Busca la vista de una cuenta por su identificador de dominio.
     *
     * @param id Identificador de la cuenta.
     * @return Optional con la vista de la cuenta si existe.
     */
    Optional<AccountView> findById(AccountId id);
}
