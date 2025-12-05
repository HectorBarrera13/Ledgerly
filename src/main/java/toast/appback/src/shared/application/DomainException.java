package toast.appback.src.shared.application;

import toast.appback.src.shared.domain.DomainError;

import java.util.List;

/**
 * Excepción que representa fallos de validación o reglas de negocio del dominio
 * detectadas durante la ejecución de casos de uso.
 */
public class DomainException extends RuntimeException {
    private final List<DomainError> errors;

    public DomainException(List<DomainError> errors) {
        super("Domain exception occurred");
        this.errors = errors;
    }

    public List<DomainError> getErrors() {
        return errors;
    }
}
