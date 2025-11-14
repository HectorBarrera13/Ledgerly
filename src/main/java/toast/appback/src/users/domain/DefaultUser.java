package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.event.UserCreated;
import java.util.List;

public class DefaultUser extends UserFactory {
    private Result<User, DomainError> create(String firstName, String lastName, String phoneCode, String phoneNumber) {
        Result<User, DomainError> user = Name.create(firstName, lastName)
                        .flatMap(_name -> Phone.create(phoneCode, phoneNumber)
                                .map(_phone-> new User(
                                            UserId.generate(),
                                            _name,
                                            _phone,
                                            List.of()
                                    )));
        user.ifSuccess(u -> u.recordEvent(new UserCreated(
                u.getUserId(),
                u.getName()
        )));
        return user;
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
