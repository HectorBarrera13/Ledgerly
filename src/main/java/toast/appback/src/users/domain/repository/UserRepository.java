package toast.appback.src.users.domain.repository;

import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UserId id);
}