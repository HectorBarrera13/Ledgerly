package toast.appback.src.users.application.mother;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.*;

public class UserMother {

    public static User validUser() {
        return create(
                new CreateUserCommand(
                        "John",
                        "Doe",
                        "+23",
                        "1234567890"
                )
        ).orElseThrow(result -> new IllegalStateException("UserMother failed to create valid user\n"+result));
    }

    public static Result<User, DomainError> create(String firstName, String lastName, String phoneCode, String phoneNumber) {
        Result<Name, DomainError> nameResult = Name.create(firstName, lastName);
        Result<Phone, DomainError> phoneResult = Phone.create(phoneCode, phoneNumber);
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(nameResult);
        emptyResult.collect(phoneResult);

        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        Name name = nameResult.get();
        Phone phone = phoneResult.get();
        User user = User.create(name, phone);
        return Result.ok(user);
    }

    public static Result<User, DomainError> create(CreateUserCommand command) {
        return create(
                command.firstName(),
                command.lastName(),
                command.phoneCountryCode(),
                command.phoneNumber()
        );
    }
}
