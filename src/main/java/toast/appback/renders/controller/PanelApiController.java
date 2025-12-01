package toast.appback.renders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toast.appback.renders.dto.*;
import toast.appback.renders.service.PanelDataService;

import java.util.List;

@RestController
@RequestMapping("/panel")
@RequiredArgsConstructor
public class PanelApiController {

    private final PanelDataService panelDataService;

    @GetMapping("/system-info")
    public SystemInfoResponse getSystemInfo() {
        return panelDataService.getSystemInfo();
    }

    @GetMapping("/metrics")
    public MetricsResponse getMetrics() {
        return panelDataService.getMetrics();
    }

    @GetMapping("/users")
    public List<UserSummaryResponse> getUsers() {
        return panelDataService.getUsers();
    }

    @GetMapping("/logs")
    public List<LogEntryResponse> getLogs(@RequestParam(defaultValue = "100") int limit) {
        return panelDataService.getLogs(limit);
    }

    @PostMapping("/clear-cache")
    public void clearCache() {
        panelDataService.clearCache();
    }
}

