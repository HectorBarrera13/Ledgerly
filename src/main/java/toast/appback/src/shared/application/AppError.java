package toast.appback.src.shared.application;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.errors.IError;

import java.util.List;
import java.util.Objects;

public record AppError(String message, String details, AppErrType type, String field) implements IError {

    public static AppError domainError(DomainError domainError) {
        AppErrType type = switch (domainError.type()) {
            case VALIDATION_ERROR -> AppErrType.NOT_PROCESSABLE_ENTITY;
            case BUSINESS_RULE_VIOLATION -> AppErrType.FORBIDDEN_OPERATION;
            case UNEXPECTED_ERROR -> AppErrType.UNKNOWN_ERROR;
        };
        return new AppError(
                domainError.message(),
                domainError.details(),
                type,
                domainError.field()
        );
    }

    public static List<AppError> domainError(List<DomainError> domainError) {
        return domainError.stream().map(AppError::domainError).toList();
    }

    public static AppError validation(String field, String message) {
        return new AppError(message, null, AppErrType.VALIDATION_ERROR, field);
    }

    public static AppError entityNotFound(String field, String message) {
        return new AppError(message, null, AppErrType.ENTITY_NOT_FOUND, field);
    }

    public static AppError entityNotFound(String field) {
        return new AppError(null, null, AppErrType.ENTITY_NOT_FOUND, field);
    }

    public static AppError authorizationFailed(String message) {
        return new AppError(message, null, AppErrType.AUTHORIZATION_FAILED, null);
    }

    public static AppError dataIntegrityViolation(String message) {
        return new AppError(message, null, AppErrType.DATA_INTEGRITY_VIOLATION, null);
    }

    public static AppError externalServiceError(String message) {
        return new AppError(message, null, AppErrType.EXTERNAL_SERVICE_ERROR, null);
    }

    public static AppError unknownError(String message) {
        return new AppError(message, null, AppErrType.UNKNOWN_ERROR, null);
    }

    public AppError withDetails(String details) {
        return new AppError(
                message(),
                details(),
                type(),
                field()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AppError appError)) return false;
        return Objects.equals(field, appError.field) && Objects.equals(message, appError.message) && type == appError.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type, field);
    }
}
