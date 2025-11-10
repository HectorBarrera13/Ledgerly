package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.application.communication.result.UserQueryResult;
import toast.appback.src.users.application.port.UserQueryRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryMySQL implements UserQueryRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<UserQueryResult> getUser(UserId userId) {
        return jpaUserRepository.findByUserId(userId.getValue())
                .map(user -> new UserQueryResult(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getFirstName(),
                        user.getPhoneAsString()
                ));
    }
}
