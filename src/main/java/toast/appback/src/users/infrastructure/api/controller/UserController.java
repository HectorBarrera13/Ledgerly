package toast.appback.src.users.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toast.appback.src.users.application.port.UserReadRepository;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserReadRepository userReadRepository;


    @GetMapping("/count")
    public Long getUserCount() {
        return userReadRepository.count();
    }
}
