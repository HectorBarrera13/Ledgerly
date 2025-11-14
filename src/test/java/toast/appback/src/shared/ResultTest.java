package toast.appback.src.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.ResultAggregator;
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

    @Test
    @DisplayName("Valid Result imperative Test")
    void imperativeSampleTest() {
        ResultAggregator<DomainError> resultAggregator = Result.aggregator();
        Result<Name, DomainError> nameResult = Name.create(firstName, lastName);
        resultAggregator.add(nameResult);
        Result<Phone, DomainError> phoneResult = Phone.create(countryCode, phoneNumber);
        resultAggregator.add(phoneResult);
        assert !resultAggregator.hasErrors();
    }

    @Test
    @DisplayName("Compare chaining and imperative styles for invalid inputs")
    void compareChainingAndImperativeStyles() {
        // Using chaining
        Result<User, DomainError> result = Result.<DomainError>chain()
                .and(() -> Name.create(invalidFirstName, invalidLastName))
                .and(() -> Phone.create(invalidCountryCode, invalidPhoneNumber))
                .result((name, phone) -> new User(
                        UserId.generate(),
                        name,
                        phone
                ));

        // Using imperative style
        ResultAggregator<DomainError> resultAggregator = Result.aggregator();
        Result<Name, DomainError> nameResult = Name.create(invalidFirstName, invalidLastName);
        resultAggregator.add(nameResult);
        Result<Phone, DomainError> phoneResult = Phone.create(invalidCountryCode, invalidPhoneNumber);
        resultAggregator.add(phoneResult);
        assert result.isFailure() && resultAggregator.hasErrors();
        assert result.getErrors().equals(resultAggregator.getErrors());
    }
}
