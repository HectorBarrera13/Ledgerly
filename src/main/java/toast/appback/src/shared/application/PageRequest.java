package toast.appback.src.shared.application;

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

    public int getOffset() {
        return page * size;
    }
}
