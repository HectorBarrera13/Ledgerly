package toast.appback.src.shared.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Pageable<T, C>(
        List<T> items,
        @JsonProperty("next_cursor")
        C nextCursor
) {
}
