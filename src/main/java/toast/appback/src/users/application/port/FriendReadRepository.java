package toast.appback.src.users.application.port;

import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

/**
 * Puerto de lectura para obtener listados de amigos y búsquedas relacionadas.
 * <p>
 * Implementaciones deben soportar paginación y búsqueda por nombre o teléfono.
 */
public interface FriendReadRepository {
    /**
     * Obtiene amigos de un usuario con paginación básica.
     *
     * @param userId      Identificador del usuario dueño de los amigos.
     * @param pageRequest Parámetros de paginación (tamaño y offset/page).
     * @return Resultado paginado con vistas de amigos.
     */
    PageResult<FriendView, UUID> findFriendsByUserId(UserId userId, PageRequest pageRequest);

    /**
     * Obtiene amigos de un usuario después de un cursor para paginación incremental.
     *
     * @param userId        Identificador del usuario.
     * @param cursorRequest Petición con cursor y tamaño.
     * @return Resultado paginado a partir del cursor.
     */
    PageResult<FriendView, UUID> findFriendsByUserIdAfterCursor(UserId userId, CursorRequest<UUID> cursorRequest);

    /**
     * Busca amigos por nombre con paginación.
     *
     * @param userId      Dueño de la lista.
     * @param nameQuery   Consulta de nombre (puede ser parte del nombre).
     * @param pageRequest Parámetros de paginación.
     * @return Resultado paginado con coincidencias.
     */
    PageResult<FriendView, UUID> searchFriendsByName(UserId userId, String nameQuery, PageRequest pageRequest);

    /**
     * Busca amigos por teléfono con paginación.
     *
     * @param userId      Dueño de la lista.
     * @param phoneQuery  Consulta de teléfono.
     * @param pageRequest Parámetros de paginación.
     * @return Resultado paginado con coincidencias.
     */
    PageResult<FriendView, UUID> searchFriendsByPhone(UserId userId, String phoneQuery, PageRequest pageRequest);
}
