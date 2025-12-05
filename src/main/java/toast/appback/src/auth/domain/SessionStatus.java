package toast.appback.src.auth.domain;

/**
 * Estado de una sesión dentro del agregado {@link Session}.
 */
public enum SessionStatus {
    /**
     * Sesión activa y válida.
     */
    NORMAL,
    /**
     * Sesión revocada y no válida.
     */
    REVOKED
}
