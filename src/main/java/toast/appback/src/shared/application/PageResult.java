package toast.appback.src.shared.application;

/**
 * Contenedor para resultados paginados.
 *
 * @param items      Lista de elementos de la página.
 * @param nextCursor Cursor para obtener la siguiente página.
 * @param <T>        Tipo de elementos.
 * @param <C>        Tipo del cursor.
 */
public record PageResult<T, C>(
        java.util.List<T> items,
        C nextCursor
) {
    /**
     * Número de elementos en la página.
     */
    public long itemCount() {
        return items().size();
    }

    /**
     * Indica si la página está vacía.
     */
    public boolean isEmpty() {
        return items().isEmpty();
    }

    /**
     * Transforma los elementos de la página aplicando el mapeador y conservando el mismo cursor.
     */
    public <R> PageResult<R, C> mapItems(java.util.function.Function<? super T, R> mapper) {
        java.util.List<R> mappedItems = new java.util.ArrayList<R>();
        for (T item : items()) {
            mappedItems.add(mapper.apply(item));
        }
        return new PageResult<>(mappedItems, nextCursor);
    }
}
