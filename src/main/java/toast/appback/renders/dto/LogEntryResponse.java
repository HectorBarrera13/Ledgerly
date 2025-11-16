package toast.appback.renders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryResponse {
    private LocalDateTime timestamp;
    private String level;
    private String message;
    private String logger;
}

