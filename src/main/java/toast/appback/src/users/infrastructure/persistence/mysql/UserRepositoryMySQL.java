package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.appback.src.users.infrastructure.persistence.mapping.UserMapper;
import toast.model.entities.users.UserEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryMySQL implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public void save(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        jpaUserRepository.save(userEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findByUserId(id.getValue())
                .map(UserMapper::toDomain);
    }
}
