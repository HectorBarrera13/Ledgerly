package toast.appback.src.shared.errors;

import java.util.Optional;

public enum AppErrType implements ErrorTypeV {
    ENTITY_NOT_FOUND,
    VALIDATION_ERROR,
    NOT_PROCESSABLE_ENTITY,
    FORBIDDEN_OPERATION,
    AUTHENTICATION_FAILED,
    AUTHORIZATION_FAILED,
    DATA_INTEGRITY_VIOLATION,
    EXTERNAL_SERVICE_ERROR,
    UNKNOWN_ERROR
    ;

    @Override
    public Optional<String> get() {
        return Optional.of(this.name());
    }
}
