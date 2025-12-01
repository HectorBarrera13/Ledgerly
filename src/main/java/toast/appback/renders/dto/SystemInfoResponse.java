package toast.appback.renders.dto;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class SystemInfoResponse {
    private long maxMemory;
    private long freeMemory;
    private long totalMemory;
    private String memoryUsage;
    private int processors;
    private String os;
    private String javaVersion;
}



