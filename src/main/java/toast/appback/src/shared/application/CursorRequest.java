package toast.appback.src.shared.application;

/**
 * Representa una petición de paginación basada en cursor.
 *
 * @param limit  Límite de elementos a recuperar.
 * @param cursor Cursor desde el que continuar (tipo genérico).
 * @param <C>    Tipo del cursor.
 */
public record CursorRequest<C>(
        Integer limit,
        C cursor
) {

    public static <C> CursorRequest<C> of(Integer limit, C cursor) {
        return new CursorRequest<>(limit, cursor);
    }
}
