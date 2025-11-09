package toast.appback.src.shared.domain;

import toast.appback.src.shared.errors.IError;

import java.util.Objects;

public record DomainError(String message, String details, DomainErrType type, String field) implements IError {

    public static DomainError validation(String field, String message) {
        return new DomainError(message, null, DomainErrType.VALIDATION_ERROR, field);
    }

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
    public boolean equals(Object o) {
        if (!(o instanceof DomainError that)) return false;
        return Objects.equals(field, that.field) && Objects.equals(message, that.message) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type, field);
    }
}
