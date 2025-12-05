package toast.appback.src.shared.application;

/**
 * Representa una petición de paginación basada en número de página.
 *
 * @param page Índice de la página (0-based).
 * @param size Tamaño de página (número de elementos por página).
 */
public record PageRequest(
        Integer page,
        Integer size
) {

    public PageRequest {
        if (page == null || page < 0) {
            throw new IllegalArgumentException("Page must be a non-negative integer");
        }
        if (size == null || size <= 0) {
            throw new IllegalArgumentException("Size must be a positive integer");
        }
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    /**
     * Offset (desplazamiento) calculado en base a la página y el tamaño.
     */
    public int getOffset() {
        return page * size;
    }
}
