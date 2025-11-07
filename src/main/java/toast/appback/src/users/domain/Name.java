package toast.appback.src.users.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.Validators;
import toast.appback.src.shared.errors.DomainError;

public record Name(String firstName, String lastName) {
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static Result<Name, DomainError> create(String firstName, String lastName) {
        return Result.combine(
                generalValidationAndNormalization(firstName, FIELD_FIRST_NAME)
                        .safeFlatMap(firstNameNormalized -> validate(firstNameNormalized, FIELD_FIRST_NAME)),
                generalValidationAndNormalization(lastName, FIELD_LAST_NAME)
                        .safeFlatMap(lastNameNormalized -> validate(lastNameNormalized, FIELD_LAST_NAME))
                ).map(r -> new Name(r.first(), r.second()));
    }

    private static Result<String, DomainError> generalValidationAndNormalization(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return Validators.EMPTY_VALUE(fieldName);
        }
        return Result.success(format(value));
    }

    private static Result<String, DomainError> validate(String value, String fieldName) {

        if (value.length() < 2) {
            return Validators.TOO_SHORT(fieldName, value, 2);
        }

        if (value.length() > 100) {
            return Validators.TOO_LONG(fieldName, value, 100);
        }

        if (!value.replaceAll(" ", "").matches("^\\p{L}+$")) {
            return Validators.INVALID_FORMAT(fieldName, value, "Must contain only letters");
        }

        String[] split = value.split(" ");
        for (String part : split) {
            if (part.length() < 2) {
                return Result.failure(
                        DomainError.validation(fieldName, "Each part of the name must be at least 2 characters long, found: " + part)
                                .withDetails("Part: '" + part + "'"));
            }
        }
        return Result.success(value);
    }

    private static String format(String value) {
        return value.strip()
                .replaceAll("[\\p{Z}\\p{C}]+", " ")
                .trim();
    }
}
