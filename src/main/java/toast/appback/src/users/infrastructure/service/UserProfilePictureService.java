package toast.appback.src.users.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfilePictureService {
    private final JpaUserRepository userRepository;
    @Value("${app.base-url}")
    private String baseUri;

    public String getProfileUri(UUID userId) {
        String pictureFileName = userRepository.getProfilePictureFileNameByUserId(userId)
                .orElse(null);
        return pictureFileName == null ? null : baseUri + "files/image/profile/" + pictureFileName;
    }
}
