package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserReadRepositoryMySQL implements UserReadRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<UserView> findById(UserId userId) {
        return jpaUserRepository.findUserProjectionByUserId(userId.getValue())
                .map(projection -> new UserView(
                        projection.getUserId(),
                        projection.getFirstName(),
                        projection.getLastName(),
                        projection.getPhone()
                ));
    }

    @Override
    public Long count() {
        return jpaUserRepository.count();
    }
}
