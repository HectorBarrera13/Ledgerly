package toast.appback.src.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Result Test")
class ResultTest {
    private final String firstName = "Example";
    private final String lastName = "User";
    private final String countryCode = "+1";
    private final String phoneNumber = "1234567890";

    @Test
    @DisplayName("Valid Result Chain Test")
    void sampleTest() {
        Result<User, DomainError> result = Result.<DomainError>chain()
                .and(() -> Name.create(firstName, lastName))
                .and(() -> Phone.create(countryCode, phoneNumber))
                .result(User::create);
        assertTrue(result.isOk());
    }
}
