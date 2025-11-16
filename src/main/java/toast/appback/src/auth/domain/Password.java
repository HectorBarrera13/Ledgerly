package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.domain.DomainError;

import java.util.Objects;

public class Password {

    private final String hashedPassword;

    private Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getHashed() {
        return hashedPassword;
    }

    public static Result<Password, DomainError> fromPlain(String rawPassword, PasswordHasher hasher) {
        Result<Void, DomainError> validation = validate(rawPassword);
        if (validation.isFailure()) {
            return Result.failure(validation.getErrors());
        }
        String hashed = hasher.hash(rawPassword);
        return Result.ok(new Password(hashed));
    }

    public static Password fromHashed(String hashedPassword) {
        return new Password(hashedPassword);
    }

    private static Result<Void, DomainError> validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return Validators.EMPTY_VALUE("password");
        }
        return validateStrength(rawPassword);
    }

    private static Result<Void, DomainError> validateStrength(String password) {
        if (password.length() < 8)
            return Validators.TOO_SHORT("password", password, 8);
        if (!password.matches(".*[A-Z].*"))
            return Validators.INVALID_FORMAT("password", password, "must contain at least one uppercase letter");
        if (!password.matches(".*[0-9].*"))
            return Validators.INVALID_FORMAT("password", password, "must contain at least one digit");
        return Result.ok();
    }

    @Override
    public String toString() {
        return "Password{" +
                "hashedPassword='" + hashedPassword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Password password)) return false;
        return Objects.equals(hashedPassword, password.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hashedPassword);
    }
}
