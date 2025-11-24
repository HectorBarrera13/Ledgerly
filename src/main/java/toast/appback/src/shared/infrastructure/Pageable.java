package toast.appback.src.shared.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.shared.application.PageResult;

import java.util.List;
import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Pageable<T, C>(
        List<T> items,
        @JsonProperty("next_cursor")
        C nextCursor
) {

    public static <R, T, C> Pageable<R, C> toPageable(PageResult<T, C> result, Function<T, R> mapper) {
        List<R> mappedItems = result.items().stream().map(mapper).toList();
        return new Pageable<>(mappedItems, result.nextCursor());
    }
}
