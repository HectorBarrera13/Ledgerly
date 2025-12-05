package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;

/**
 * Repositorio de dominio para persistir y consultar relaciones de amistad entre usuarios.
 */
public interface FriendShipRepository {
    /**
     * Persiste una relación de amistad.
     *
     * @param friendship Entidad `FriendShip` a guardar.
     */
    void save(FriendShip friendship);

    /**
     * Indica si existe una relación de amistad entre dos usuarios.
     *
     * @param userIdA Identificador del primer usuario.
     * @param userIdB Identificador del segundo usuario.
     * @return true si existe la relación, false en caso contrario.
     */
    boolean existsFriendShip(UserId userIdA, UserId userIdB);

    /**
     * Elimina la relación de amistad entre dos usuarios.
     *
     * @param userIdA Identificador del primer usuario.
     * @param userIdB Identificador del segundo usuario.
     */
    void delete(UserId userIdA, UserId userIdB);
}
