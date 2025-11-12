package toast.appback.src.shared.domain;

import toast.appback.src.shared.errors.ErrorTypeV;

import java.util.Optional;

public enum DomainErrType implements ErrorTypeV {
    VALIDATION_ERROR,
    BUSINESS_RULE_VIOLATION,
    UNEXPECTED_ERROR
    ;

    @Override
    public Optional<String> get() {
        return Optional.of(name());
    }
}
