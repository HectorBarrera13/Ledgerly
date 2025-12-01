package toast.appback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"toast.model"})
public class AppBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppBackApplication.class, args);
    }
}
