package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.domain.DomainError;

import java.util.regex.Pattern;

public record Name(String firstName, String lastName) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 80;
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final Pattern NAME_PATTERN = Pattern.compile("^(?!.* {2,})(?!.*['-]{2,})\\p{L}+(?:[ '-]\\p{L}+)*$");

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static Result<Name, DomainError> create(String firstName, String lastName) {
        Result<Void, DomainError> firstNameValidation = generalValidation(firstName, FIELD_FIRST_NAME)
                .captureFirstError(() -> validate(firstName, FIELD_FIRST_NAME));
        Result<Void, DomainError> lastNameValidation = generalValidation(lastName, FIELD_LAST_NAME)
                .captureFirstError(() -> validate(lastName, FIELD_LAST_NAME));
        return firstNameValidation
                .andThen(() -> lastNameValidation)
                .map(() -> new Name(firstName, lastName));
    }

    public static Name load(String firstName, String lastName) {
        return new Name(firstName, lastName);
    }

    private static Result<Void, DomainError> generalValidation(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return Validators.EMPTY_VALUE(fieldName);
        }
        return Result.success();
    }

    private static Result<Void, DomainError> validate(String value, String fieldName) {
        if (value.length() < MIN_LENGTH) {
            return Validators.TOO_SHORT(fieldName, value, MIN_LENGTH);
        }

        if (value.length() >= MAX_LENGTH) {
            return Validators.TOO_LONG(fieldName, value, MAX_LENGTH);
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            return Validators.INVALID_FORMAT(fieldName, value, "must contain only letters");
        }

        String[] split = value.split(" ");
        for (String part : split) {
            if (part.length() < 2) {
                return Result.failure(
                        DomainError.validation(fieldName, "each part of the name must be at least 2 characters long, found: " + part)
                                .withDetails("part: '" + part + "'"));
            }
        }
        return Result.success();
    }
}
