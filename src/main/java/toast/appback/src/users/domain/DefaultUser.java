package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.event.UserCreated;

public class DefaultUser extends UserFactory {
    private Result<User, DomainError> create(String firstName, String lastName, String phoneCode, String phoneNumber) {
        Result<Name, DomainError> nameResult = Name.create(firstName, lastName);
        Result<Phone, DomainError> phoneResult = Phone.create(phoneCode, phoneNumber);
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(nameResult);
        emptyResult.collect(phoneResult);

        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        Name name = nameResult.getValue();
        Phone phone = phoneResult.getValue();
        User user = new User(
                UserId.generate(),
                name,
                phone
        );
        user.recordEvent(
                new UserCreated(
                        user.getUserId(),
                        user.getName()
                )
        );
        return Result.success(user);
    }

    @Override
    public Result<User, DomainError> create(CreateUserCommand command) {
        return create(
                command.firstName(),
                command.lastName(),
                command.phoneCountryCode(),
                command.phoneNumber()
        );
    }
}
