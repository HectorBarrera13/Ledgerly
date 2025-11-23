package toast.appback.src.shared.application;

import java.util.List;
import java.util.function.Function;

public record PageResult<T, C>(
        List<T> items,
        C nextCursor
) {
    public long itemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public <R> PageResult<R, C> mapItems(Function<? super T, R> mapper) {
        List<R> mappedItems = items.stream()
                .map(mapper)
                .toList();
        return new PageResult<>(mappedItems, nextCursor);
    }
}
