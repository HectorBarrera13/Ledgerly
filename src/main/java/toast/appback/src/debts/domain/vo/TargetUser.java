package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

/**
 * Value Object que representa el nombre del "usuario objetivo" en una deuda rápida.
 *
 * Asegura inmutabilidad y valida:
 *  - Que el nombre no sea nulo ni vacío
 *  - Que no exceda la longitud máxima permitida
 */
public class TargetUser {
    // Nombre del usuario objetivo (inmutable)
    private final String name;

    private static final String FIELD_NAME = "name";
    private static final int MAX_NAME_LENGTH = 30;

    /**
     * Constructor privado para asegurar creación controlada vía create() o load().
     */
    private TargetUser(String name) {
        this.name = name;
    }

    /**
     * Factory method principal.
     * Valida:
     *  - name no debe ser nulo ni vacío
     *  - name no debe exceder MAX_NAME_LENGTH
     *
     * Devuelve Result<> para manejar errores sin excepciones en dominio.
     */
    public static Result<TargetUser, DomainError> create(String name) {
        Result<String, DomainError> emptyResult = Result.empty();
        emptyResult.collect(nameValidation(name, FIELD_NAME));

        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        return Result.ok(new TargetUser(name));
    }

    /**
     * Factory method para reconstrucción desde persistencia.
     * No valida porque se asume que los datos ya fueron verificados previamente.
     */
    public static TargetUser load(String name) {
        return new TargetUser(name);
    }

    /**
     * Reglas de validación para el nombre.
     */
    public static Result<String, DomainError> nameValidation(String name, String field) {
        if (name == null || name.isEmpty()) {
            return Validators.EMPTY_VALUE(field);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return Validators.TOO_LONG(field, name, MAX_NAME_LENGTH);
        }
        return Result.ok();
    }

    public String getName() {
        return name;
    }
}
