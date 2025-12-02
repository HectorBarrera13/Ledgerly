package toast.appback.src.auth.domain;

import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.util.Objects;

public class Password {
    private static final String FIELD_NAME = "password";

    private final String hashedPassword;

    private Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
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
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or blank");
        }
        return new Password(hashedPassword);
    }

    private static Result<Void, DomainError> validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return Validators.EMPTY_VALUE(FIELD_NAME);
        }
        return validateStrength(rawPassword);
    }

    private static Result<Void, DomainError> validateStrength(String password) {
        if (password.length() < 8)
            return Validators.TOO_SHORT(FIELD_NAME, password, 8);

        // Usar búsqueda directa en lugar de regex vulnerable
        if (!containsUppercase(password))
            return Validators.INVALID_FORMAT(FIELD_NAME, password, "must contain at least one uppercase letter");

        if (!containsDigit(password))
            return Validators.INVALID_FORMAT(FIELD_NAME, password, "must contain at least one digit");

        return Result.ok();
    }

    private static boolean containsUppercase(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsDigit(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public String getHashed() {
        return hashedPassword;
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
