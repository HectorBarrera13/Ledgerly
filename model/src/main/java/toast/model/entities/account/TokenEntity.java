package toast.model.entities.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity {

    private UUID sessionId;

    private String refresh;

    private String type;

    private Instant expiresAt;
}
