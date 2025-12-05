package toast.appback.src.debts.application.port.services;

import java.util.UUID;

/**
 * Servicio de consulta para obtener información de usuarios desde otros subsistemas.
 * <p>
 * Implementaciones deben abstraer la manera en que se obtiene el nombre de usuario (repositorio, API, etc.).
 */
public interface UserQueryService {
    /**
     * Devuelve el nombre completo o representación del usuario indicado.
     *
     * @param userId Identificador del usuario.
     * @return Nombre/representación del usuario.
     */
    String getUserNameById(UUID userId);
}
