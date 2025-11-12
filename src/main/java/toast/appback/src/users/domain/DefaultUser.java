package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.event.UserCreated;
import java.util.List;

public class DefaultUser extends UserFactory {
    @Override
    public Result<User, DomainError> create(String firstName, String lastName, String phoneCode, String phoneNumber) {
        Result<User, DomainError> user = Name.create(firstName, lastName)
                        .flatMap(_name -> Phone.create(phoneCode, phoneNumber)
                                .map(_phone-> new User(
                                            UserId.generate(),
                                            _name,
                                            _phone,
                                            List.of()
                                    )));
        user.ifSuccess(u -> u.recordEvent(new UserCreated(
                u.getId(),
                u.getName()
        )));
        return user;
    }
}
