package toast.appback.src.shared.infrastructure;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileServiceController {

    @Value("${file.upload-dir}")
    private String path;

    private Path fileStorageLocation;


    @PostConstruct
    public void init() {
        fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
    }

    @GetMapping("/image/profile/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String filename
    ) {
        try {
            // Resolve path + /image/profile dir
            Path filePath = fileStorageLocation.resolve("image/profile").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = "image/jpeg"; // Default content type
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
