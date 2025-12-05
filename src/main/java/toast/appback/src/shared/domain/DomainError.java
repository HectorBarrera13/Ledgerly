package toast.appback.src.shared.domain;

import toast.appback.src.shared.errors.IError;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un error de dominio con metadatos adicionales útiles para validaciones y
 * para mapear errores hacia capas superiores.
 * <p>
 * Campos principales:
 * - message: mensaje técnico o descriptivo del error.
 * - details: información adicional opcional (por ejemplo, valor inválido).
 * - type: tipo lógico del error ({@link DomainErrType}).
 * - field: campo afectado (si aplica).
 * - validatorType: categoría concreta de validación ({@link ValidatorType}).
 * - businessCode: código de negocio opcional para clasificar errores previstos.
 */
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

    /**
     * Fabrica un error de validación simple asociado a un campo.
     */
    public static DomainError validation(String field, String message) {
        return new DomainError(message, null, DomainErrType.VALIDATION_ERROR, field, ValidatorType.UNEXPECTED_ERROR, null);
    }

    /**
     * Fabrica un error por violación de regla de negocio.
     */
    public static DomainError businessRule(String message) {
        return new DomainError(message, null, DomainErrType.BUSINESS_RULE_VIOLATION, null, ValidatorType.BUSINESS_RULE_VIOLATION, null);
    }

    /**
     * Fabrica un error vacío/por defecto.
     */
    public static DomainError empty() {
        return new DomainError("", null, DomainErrType.UNEXPECTED_ERROR, null, ValidatorType.UNEXPECTED_ERROR, null);
    }

    /**
     * Devuelve una copia del error con información adicional en el campo details.
     */
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

    /**
     * Devuelve una copia del error con un {@link ValidatorType} concreto.
     */
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

    /**
     * Devuelve una copia del error con un código de negocio.
     */
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
