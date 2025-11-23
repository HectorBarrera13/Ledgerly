package toast.model.entities.users;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PhoneEmbeddable {
    private String countryCode;
    private String number;

    public static PhoneEmbeddable create(String countryCode, String number) {
        PhoneEmbeddable phone = new PhoneEmbeddable();
        phone.setCountryCode(countryCode);
        phone.setNumber(number);
        return phone;
    }

    public String getFullNumber() {
        return countryCode + "-" + number;
    }
}
