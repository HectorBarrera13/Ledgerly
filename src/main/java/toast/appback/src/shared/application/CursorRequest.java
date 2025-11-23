package toast.appback.src.shared.application;

public record CursorRequest<C>(
        Integer limit,
        C cursor
) {

    public static <C> CursorRequest<C> of(Integer limit, C cursor) {
        return new CursorRequest<>(limit, cursor);
    }
}
