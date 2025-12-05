package toast.appback.src.users.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.UUID;

/**
 * Servicio que encapsula la resolución de la URL pública de la foto de perfil de un usuario.
 * <p>
 * Devuelve null si no existe fichero de imagen asociado.
 */
@Service
@RequiredArgsConstructor
public class UserProfilePictureService {
    private final JpaUserRepository userRepository;
    @Value("${app.base-url}")
    private String baseUri;

    /**
     * Obtiene la URI pública de la imagen de perfil del usuario.
     *
     * @param userId Identificador del usuario.
     * @return URI pública o null si no existe.
     */
    public String getProfileUri(UUID userId) {
        String pictureFileName = userRepository.getProfilePictureFileNameByUserId(userId)
                .orElse(null);
        return pictureFileName == null ? null : baseUri + "files/image/profile/" + pictureFileName;
    }
}
