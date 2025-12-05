package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

/**
 * Repositorio de dominio para persistir y recuperar entidades `User`.
 * <p>
 * Implementaciones deben encargarse de la persistencia transaccional y rehidratación de la entidad.
 */
public interface UserRepository {
    /**
     * Persiste o actualiza la entidad de usuario.
     *
     * @param user Entidad de dominio a guardar.
     */
    void save(User user);

    /**
     * Busca un usuario por su identificador de dominio.
     *
     * @param id Identificador del usuario.
     * @return Optional con la entidad si existe.
     */
    Optional<User> findById(UserId id);
}