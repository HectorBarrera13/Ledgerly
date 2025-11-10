package toast.appback.src.shared.domain;

import toast.appback.src.shared.errors.IError;

import java.util.Objects;

public record DomainError(String message, String details, DomainErrType type, String field, ValidatorType validatorType) implements IError {

    public static DomainError validation(String field, String message) {
        return new DomainError(message, null, DomainErrType.VALIDATION_ERROR, field, ValidatorType.INVALID_FORMAT);
    }

    public static DomainError businessRule(String message) {
        return new DomainError(message, null, DomainErrType.BUSINESS_RULE_VIOLATION, null, ValidatorType.BUSINESS_RULE_VIOLATION);
    }

    public static DomainError unexpected(String message, String details) {
        return new DomainError(message, details, DomainErrType.UNEXPECTED_ERROR, null, ValidatorType.UNEXPECTED_ERROR);
    }


    public DomainError withDetails(String details) {
        return new DomainError(
                message(),
                details,
                type(),
                field(),
                validatorType()
        );
    }

    public DomainError withValidatorType(ValidatorType validatorType) {
        return new DomainError(
                message(),
                details(),
                type(),
                field(),
                validatorType
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
