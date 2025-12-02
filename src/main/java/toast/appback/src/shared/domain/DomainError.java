package toast.appback.src.shared.domain;

import toast.appback.src.shared.errors.IError;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record DomainError(
        String message,
        String details,
        DomainErrType type,
        String field,
        ValidatorType validatorType,
        BusinessCode businessCode // optional
) implements IError, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static DomainError validation(String field, String message) {
        return new DomainError(message, null, DomainErrType.VALIDATION_ERROR, field, ValidatorType.UNEXPECTED_ERROR, null);
    }

    public static DomainError businessRule(String message) {
        return new DomainError(message, null, DomainErrType.BUSINESS_RULE_VIOLATION, null, ValidatorType.BUSINESS_RULE_VIOLATION, null);
    }

    public static DomainError empty() {
        return new DomainError("", null, DomainErrType.UNEXPECTED_ERROR, null, ValidatorType.UNEXPECTED_ERROR, null);
    }


    public DomainError withDetails(String details) {
        return new DomainError(
                message(),
                details,
                type(),
                field(),
                validatorType(),
                businessCode()
        );
    }

    public DomainError withValidatorType(ValidatorType validatorType) {
        return new DomainError(
                message(),
                details(),
                type(),
                field(),
                validatorType,
                businessCode()
        );
    }

    public DomainError withBusinessCode(BusinessCode businessCode) {
        return new DomainError(
                message(),
                details(),
                type(),
                field(),
                validatorType(),
                businessCode
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
