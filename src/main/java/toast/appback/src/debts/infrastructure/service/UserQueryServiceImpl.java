package toast.appback.src.debts.infrastructure.service;

import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.port.services.UserQueryService;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.UUID;

@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getUserNameById(UUID userId) {
        var id = UserId.load(userId);
        return userRepository.findById(id)
                .map(User::getName)
                .map(Name::getFullName)
                .orElseThrow(() -> new UserNotFound( id ));
    }
}
