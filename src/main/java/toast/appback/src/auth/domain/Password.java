package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.Validators;
import toast.appback.src.shared.errors.DomainError;

public record Password(
        String hashed
) {
    public static Result<Password, DomainError> fromPlain(String rawPassword, PasswordHasher hasher) {
        return validateStrength(rawPassword)
                .flatMap(() -> {
                    String hashedPassword = hasher.hash(rawPassword);
                    return Result.success(new Password(hashedPassword));
                });
    }

    public static Password fromHashed(String hashedPassword) {
        return new Password(hashedPassword);
    }

    private static Result<Void, DomainError> validateStrength(String password) {
        if (password.length() < 8)
            return Validators.TOO_SHORT("password", password, 8);
        if (!password.matches(".*[A-Z].*"))
            return Validators.INVALID_FORMAT("password", password, "Must contain at least one uppercase letter");
        if (!password.matches(".*[0-9].*"))
            return Validators.INVALID_FORMAT("password", password, "Must contain at least one digit");
        return Result.success();
    }
}
