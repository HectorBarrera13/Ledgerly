package toast.appback.renders.persistence.mysql;

import org.springframework.stereotype.Repository;
import toast.appback.renders.persistence.repositories.UserDataRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

@Repository
public class UserDataMySQL implements UserDataRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserDataMySQL(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public long countUsers() {
        return jpaUserRepository.count();
    }
}
