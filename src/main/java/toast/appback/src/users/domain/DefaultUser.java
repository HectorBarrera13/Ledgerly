package toast.appback.src.users.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.domain.event.UserCreated;

import java.util.ArrayList;
import java.util.UUID;

public class DefaultUser extends UserFactory {
    @Override
    public Result<User, DomainError> create(UUID uuid, String firstName, String lastName, String phoneCode, String phoneNumber) {
        Result<User, DomainError> user = Name.create(firstName, lastName)
                        .flatMap(_name -> Phone.create(phoneCode, phoneNumber)
                                .map(_phone-> new User(
                                            UserId.create(uuid),
                                            _name,
                                            _phone,
                                            new ArrayList<>())
                                    ));
        user.ifSuccess(u -> u.addDomainEvent(new UserCreated(
                u.getId(),
                u.getName()
        )));
        return user;
    }
}
