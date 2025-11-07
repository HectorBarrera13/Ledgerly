package toast.appback.src.shared.errors;

import java.util.Objects;

/**
 * Class representing a domain error with details such as message, type, field, and additional details.
 * <p>
 * {@link DomainErrType} enum defines the type of domain error.
 * {@link IError} interface defines the contract for error classes.
 * </p>
 */
public record DomainError(String message, String details, DomainErrType type, String field) implements IError {

    // This is a factory method to create a validation error
    public static DomainError validation(String field, String message) {
        return new DomainError(message, null, DomainErrType.VALIDATION_ERROR, field);
    }

    // This is a factory method to create a business rule violation error
    public static DomainError businessRule(String message) {
        return new DomainError(message, null, DomainErrType.BUSINESS_RULE_VIOLATION, null);
    }

    public static DomainError unexpected(String message, String details) {
        return new DomainError(message, details, DomainErrType.UNEXPECTED_ERROR, null);
    }


    public DomainError withDetails(String details) {
        return new DomainError(
                message(),
                details(),
                type(),
                field()
        );
    }

    @Override
    public String toString() {
        return "ServiceError{" +
                "message='" + message() + '\'' +
                ", details='" + details() + '\'' +
                ", type='" + type().get() + '\'' +
                ", field='" + field() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainError that)) return false;
        return message.equals(that.message) &&
                type.equals(that.type) &&
                field.equals(that.field) &&
                (Objects.equals(details, that.details));
    }

}
