package toast.appback.src.users.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;

import java.util.UUID;

public abstract class UserFactory {
    public abstract Result<User, DomainError> create(
            UUID id,
            String firstName,
            String lastName,
            String phoneCode,
            String phoneNumber
    );
}
