package toast.appback.renders.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;

@Service
public class ServerDataService {
    @Value("${info.app.name}")
    private String serverName;

    @Value("${info.app.instance}")
    private String instance;

    @Value("${info.app.location}")
    private String location;

    @Value("${info.app.api.url}")
    private String apiUrl;

    @Value("${info.app.version}")
    private String version;

    public void populateModelWithServerData(Model model) {
        String upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + " seconds";
        RestTemplate restTemplate = new RestTemplate();
        long startTime = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.getForEntity("https://www.google.com", String.class);
        long latency = System.currentTimeMillis() - startTime;
        model.addAttribute("apiUrl", apiUrl);
        model.addAttribute("serverName", serverName);
        model.addAttribute("instance", instance);
        model.addAttribute("location", location);
        model.addAttribute("uptime", upTime);
        model.addAttribute("version", version);
        model.addAttribute("latency", latency + " ms");
    }
}
