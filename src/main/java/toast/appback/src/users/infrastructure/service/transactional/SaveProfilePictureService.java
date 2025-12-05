package toast.appback.src.users.infrastructure.service.transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaveProfilePictureService {
    private final JpaUserRepository userRepository;
    @Value("${file.upload-dir}")
    private String filePath;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        fileStorageLocation = Paths.get(filePath).toAbsolutePath().normalize().resolve("image/profile");
    }

    @Transactional
    public void execute(UserId userId, byte[] pictureData, String mediaType) {
        // Logic to save the profile picture data, e.g., store in a file system or cloud storage
        // and update the user's profilePictureUrl in the database.

        if (!isValidType(mediaType)) {
            throw new IllegalArgumentException("Invalid image type");
        }

        var userEntity = userRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String extension = getExtension(mediaType);
        String pictureName = UUID.randomUUID() + "." + extension;

        userEntity.setProfilePictureFileName(pictureName);
        userRepository.save(userEntity);
        saveFile(pictureData, pictureName);
    }

    private boolean isValidType(String mediaType) {
        try {
            MediaType mediaTypeParsed = MediaType.parseMediaType(mediaType);
            return mediaTypeParsed.isCompatibleWith(MediaType.IMAGE_JPEG) ||
                    mediaTypeParsed.isCompatibleWith(MediaType.IMAGE_PNG);
        } catch (Exception e) {
            return false;
        }
    }

    private String getExtension(String mediaType) {
        return switch (mediaType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        };
    }

    private void saveFile(byte[] pictureData, String fileName) {
        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            java.nio.file.Files.write(targetLocation, pictureData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save profile picture", e);
        }
    }

}
