package toast.appback.src.middleware;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

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
        HttpStatus status
) {
    public static ErrorData create(String message, List<ErrorDetails> details) {
        return new ErrorData(null, message, details, Instant.now(), null);
    }

    public static ErrorData create(String message) {
        return new ErrorData(null, message, null, Instant.now(), null);
    }

    public static ErrorData create(String fieldName, String message, HttpStatus status) {
        return new ErrorData(fieldName, message, null, Instant.now(), status);
    }
}
