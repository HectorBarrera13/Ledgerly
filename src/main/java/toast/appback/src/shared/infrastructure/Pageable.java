package toast.appback.src.shared.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Pageable<T, C>(
        List<T> items,
        @JsonProperty("next_cursor")
        C nextCursor
) {
}
