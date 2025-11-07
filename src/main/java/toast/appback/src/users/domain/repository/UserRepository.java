package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    void deleteById(UUID id);
}
