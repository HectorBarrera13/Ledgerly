package toast.appback.src.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

@DisplayName("Result Test")
public class ResultTest {
    private final String firstName = "Example";
    private final String lastName = "User";
    private final String invalidFirstName = "Ex@mpl3";
    private final String invalidLastName = "Us3r!";
    private final String countryCode = "+1";
    private final String phoneNumber = "1234567890";
    private final String invalidCountryCode = "13412";
    private final String invalidPhoneNumber = "123-456-7890";

    @Test
    @DisplayName("Valid Result Chain Test")
    void sampleTest() {
        Result<User, DomainError> result = Result.<DomainError>chain()
                .and(() -> Name.create(firstName, lastName))
                .and(() -> Phone.create(countryCode, phoneNumber))
                .result((name, phone) -> new User(
                        UserId.generate(),
                        name,
                        phone
                ));
        assert result.isSuccess();
    }
}
