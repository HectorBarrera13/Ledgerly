package toast.model.entities.users;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PhoneEmbeddable {
    private String countryCode;
    private String number;
}
