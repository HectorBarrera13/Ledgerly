package toast.appback.renders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsResponse {
    private double cpuUsage;
    private double memoryUsagePercent;
    private int threads;
    private int requestsPerMin;
    private long uptime;
}

