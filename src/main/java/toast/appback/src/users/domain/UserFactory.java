package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;

public abstract class UserFactory {
    public abstract Result<User, DomainError> create(
            String firstName,
            String lastName,
            String phoneCode,
            String phoneNumber
    );
}