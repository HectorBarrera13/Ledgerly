package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.util.Objects;
import java.util.regex.Pattern;

public class Name {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 80;
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final Pattern NAME_PATTERN = Pattern.compile("^(?!.* {2})(?!.*['-]{2})\\p{L}+(?:[ '-]\\p{L}+)*+$");
    private final String firstName;
    private final String lastName;

    private Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Result<Name, DomainError> create(String firstName, String lastName) {
        Result<Void, DomainError> result = Result.empty();
        result.collect(validation(firstName, FIELD_FIRST_NAME));
        result.collect(validation(lastName, FIELD_LAST_NAME));
        if (result.isFailure()) {
            return result.castFailure();
        }
        return Result.ok(new Name(firstName, lastName));
    }

    public static Name load(String firstName, String lastName) {
        return create(firstName, lastName)
                .orElseThrow(() -> new IllegalArgumentException("invalid name values: " + firstName + " " + lastName));
    }

    private static Result<Void, DomainError> validation(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return Validators.EMPTY_VALUE(fieldName);
        }
        String trimmed = value.trim();
        return validate(trimmed, fieldName);
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
                return Validators
                        .INVALID_FORMAT("fieldName", value, "each part of the name must be at least 2 characters long");
            }
        }
        return Result.ok();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Name{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Name name)) return false;
        return Objects.equals(firstName, name.firstName) && Objects.equals(lastName, name.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
