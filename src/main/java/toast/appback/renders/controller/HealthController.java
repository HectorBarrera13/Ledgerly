package toast.appback.renders.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import toast.appback.renders.service.ServerDataService;

@Controller()
public class HealthController {

    private final ServerDataService serverDataService;

    public HealthController(ServerDataService serverDataService) {
        this.serverDataService = serverDataService;
    }

    @GetMapping("/health")
    public String healthCheck(Model model) {
        serverDataService.populateModelWithServerData(model);
        return "health/welcome";
    }

    @GetMapping("/health/panel")
    public String healthPanel() {
        return "health/panel";
    }
}
