package toast.appback.src.middleware;

/**
 * Detalles de un error específico.
 *
 * @param field   Campo asociado al error.
 * @param message Mensaje descriptivo del error.
 */
public record ErrorDetails(
        String field,
        String message
) {
}
