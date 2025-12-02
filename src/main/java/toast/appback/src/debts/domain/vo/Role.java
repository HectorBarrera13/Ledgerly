package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

/**
 * Value Object que representa el rol dentro de una deuda:
 *  - "DEBTOR"
 *  - "CREDITOR"
 *
 * Es inmutable y asegura, mediante validación, que solo existan roles válidos.
 * Se usa en entidades como DebtBetweenUsers o QuickDebt.
 */

public class Role {
        private static final String[] validRoles = {"DEBTOR", "CREDITOR"};
        private static final String ROLE_FIELD = "role";
        public static final Role CREDITOR = new Role("CREDITOR");
        public static final Role DEBTOR = new Role("DEBTOR");

        // Valor inmutable del rol
        private final String role;

        /**
         * Constructor privado. Forzamos creación controlada mediante create() o load().
         */
        private Role(String role) {
            this.role = role;
        }

        /**
         * Factory method principal.
         * Valida que el rol:
         * - No sea vacío ni en blanco
         * - Sea uno de los permitidos ("DEBTOR", "CREDITOR")
         * <p>
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

        /**
         * Lógica de validación del rol.
         * Verifica:
         * - No estar vacío
         * - Ser uno de los roles definidos en validRoles
         */
        public static Result<String, DomainError> validateRol(String role, String field) {
            if (role.isBlank()) {
                return Validators.EMPTY_VALUE(field);
            }

            for (String validRole : validRoles) {
                if (role.equalsIgnoreCase(validRole)) {
                    return Result.ok();
                }
            }

            return Validators.INVALID_FORMAT(field, role, "Must be either DEBTOR or CREDITOR");
        }

        public String getRole() {
            return role;
        }
}
