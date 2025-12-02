package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

/**
 * Value Object que representa el rol dentro de una deuda:
 * - "DEBTOR"
 * - "CREDITOR"
 */

public class Role {
    private static final String[] validRoles = {"DEBTOR", "CREDITOR"};
    private static final String ROLE_FIELD = "role";
    // Valor inmutable del rol
    private final String value;

    /**
     * Constructor privado. Forzamos creación controlada mediante create().
     */
    private Role(String role) {
        this.value = role;
    }

    /**
     * Factory method principal.
     * Devuelve un Result para manejar errores sin usar excepciones.
     */
    public static Result<Role, DomainError> create(String role) {
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(validateRol(role, ROLE_FIELD));

        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        return Result.ok(new Role(role));
    }

    /**
     * Factory method alternativo para reconstrucción desde persistencia.
     * No valida, asume datos ya consistentes.
     */
    public static Role load(String role) {
        return new Role(role);
    }


    public static Result<String, DomainError> validateRol(String role, String field) {
        if (role.isBlank()) {
            return Validators.emptyValue(field);
        }

        for (String validRole : validRoles) {
            if (role.equalsIgnoreCase(validRole)) {
                return Result.ok();
            }
        }

        return Validators.invalidFormat(field, role, "Must be either DEBTOR or CREDITOR");
    }

    public String getValue() {
        return value;
    }
}
