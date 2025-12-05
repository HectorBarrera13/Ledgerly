package toast.appback.src.debts.infrastructure.service;

import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.port.services.UserQueryService;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.UUID;

/**
 * Implementación de servicio de consulta de usuarios usada por el módulo de deudas.
 * <p>
 * Provee utilidades de lectura sobre usuarios, por ejemplo obtener el nombre completo a partir de UUID.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene el nombre completo del usuario por su UUID.
     *
     * @param userId UUID del usuario.
     * @return Nombre completo.
     * @throws UserNotFound si no existe el usuario.
     */
    @Override
    public String getUserNameById(UUID userId) {
        var id = UserId.load(userId);
        return userRepository.findById(id)
                .map(User::getName)
                .map(Name::getFullName)
                .orElseThrow(() -> new UserNotFound(id));
    }
}
