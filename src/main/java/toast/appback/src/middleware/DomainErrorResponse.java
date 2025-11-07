package toast.appback.src.middleware;

import java.time.Instant;
import java.util.List;

public record DomainErrorResponse(
        String message,
        List<ErrorDetails> details,
        Instant timestamp
) {
}
