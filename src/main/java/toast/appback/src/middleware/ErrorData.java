package toast.appback.src.middleware;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorData(
        String field,
        String message,
        List<ErrorDetails> details,
        @JsonProperty("time_stamp")
        Instant timeStamp,
        @JsonIgnore
        int status
) {
}
