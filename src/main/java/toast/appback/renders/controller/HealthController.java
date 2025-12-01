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
    public String healthCheck() {
        return "health";
    }

    @GetMapping("/panel")
    public String healthPanel(Model model) {
        serverDataService.populateModelWithServerData(model);
        return "panel/home";
    }
}
