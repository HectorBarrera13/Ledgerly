package toast.appback.src.users.application.port;

import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

/**
 * Puerto de lectura para consultar usuarios desde la capa de aplicación.
 * <p>
 * Implementaciones deben proporcionar mecanismos de lectura optimizados para consultas públicas.
 */
public interface UserReadRepository {
    /**
     * Busca la vista del usuario por su identificador.
     *
     * @param userId Identificador del usuario.
     * @return Optional con la vista del usuario si existe.
     */
    Optional<UserView> findById(UserId userId);

    /**
     * @return Conteo total de usuarios disponibles.
     */
    Long count();
}
