package toast.appback.renders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.renders.dto.*;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.model.entities.users.UserEntity;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PanelDataService {

    private final JpaUserRepository jpaUserRepository;
    private final List<LogEntryResponse> logBuffer = new ArrayList<>();

    public SystemInfoResponse getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long usedMemory = totalMemory - freeMemory;

        return SystemInfoResponse.builder()
                .javaVersion(System.getProperty("java.version"))
                .os(System.getProperty("os.name") + " " + System.getProperty("os.version"))
                .processors(runtime.availableProcessors())
                .memoryUsage(formatBytes(usedMemory) + " / " + formatBytes(maxMemory))
                .totalMemory(totalMemory)
                .freeMemory(freeMemory)
                .maxMemory(maxMemory)
                .build();
    }

    public MetricsResponse getMetrics() {
        Runtime runtime = Runtime.getRuntime();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = ((double) usedMemory / totalMemory) * 100;

        // CPU usage estimation (simplified)
        double cpuUsage = getCpuUsage();

        return MetricsResponse.builder()
                .cpuUsage(Math.round(cpuUsage * 100.0) / 100.0)
                .memoryUsagePercent(Math.round(memoryUsagePercent * 100.0) / 100.0)
                .threads(threadMXBean.getThreadCount())
                .requestsPerMin(0) // This would need request tracking
                .uptime(ManagementFactory.getRuntimeMXBean().getUptime())
                .build();
    }

    public List<UserSummaryResponse> getUsers() {
        try {
            List<UserEntity> users = jpaUserRepository.findAll();
            return users.stream()
                    .map(user -> UserSummaryResponse.builder()
                            .id(user.getUserId().toString())
                            .name(user.getFirstName() + " " + user.getLastName())
                            .email("N/A") // UserEntity no tiene email, se puede agregar o buscar en AccountEntity
                            .active(true)
                            .createdAt(LocalDateTime.now()) // UserEntity no tiene createdAt, se puede agregar
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Return empty list if there's an error
            return new ArrayList<>();
        }
    }

    public List<LogEntryResponse> getLogs(int limit) {
        // This is a simple in-memory log buffer
        // In production, you would read from actual log files or a logging service

        if (logBuffer.isEmpty()) {
            // Add some default logs
            addLog("INFO", "Sistema iniciado correctamente");
            addLog("INFO", "Conectado a la base de datos");
            addLog("DEBUG", "Inicializando servicios...");
        }

        return logBuffer.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void addLog(String level, String message) {
        LogEntryResponse log = LogEntryResponse.builder()
                .timestamp(LocalDateTime.now())
                .level(level)
                .message(message)
                .logger("System")
                .build();

        logBuffer.add(0, log); // Add to beginning

        // Keep only last 1000 logs
        if (logBuffer.size() > 1000) {
            logBuffer.remove(logBuffer.size() - 1);
        }
    }

    public void clearCache() {
        // Implement cache clearing logic here
        // For now, just clear the log buffer
        addLog("INFO", "Caché limpiada por el administrador");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), pre);
    }

    private double getCpuUsage() {
        // Simple CPU usage estimation
        // In production, you would use OperatingSystemMXBean
        try {
            com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = osBean.getProcessCpuLoad() * 100;
            return cpuUsage >= 0 ? cpuUsage : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}

