package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

/**
 * Value Object que representa el contexto (purpose + description) asociado a una deuda.
 * Es inmutable y garantiza reglas de validación de dominio.
 */
public class Context {

    // Nombre de los campos, usados para reportar errores de validación
    private static final String FIELD_PURPOSE = "purpose";
    private static final String FIELD_DESCRIPTION = "description";

    // Límites definidos por la regla de negocio
    private static final int MAX_PURPOSE_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    // Atributos inmutables del Value Object
    private final String purpose;
    private final String description;

    /**
     * Constructor privado para asegurar creación controlada mediante factory methods.
     */
    private Context(String purpose, String description) {
        this.purpose = purpose;
        this.description = description;
    }

    /**
     * Factory method principal para crear el Value Object validando sus reglas:
     *  - purpose no puede ser nulo, vacío, ni exceder el tamaño máximo
     *  - description es opcional, pero si viene debe respetar su límite máximo
     *
     * Utiliza Result para propagar errores de dominio sin excepciones.
     */
    public static Result<Context, DomainError> create(String purpose, String description) {
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(purposeValidation(purpose, FIELD_PURPOSE));
        emptyResult.collect(descriptionValidation(description, FIELD_DESCRIPTION));

        // Si hubo errores de validación, los regresamos como DomainError
        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        // Si todas las reglas se cumplen, creamos la instancia válida
        return Result.ok(new Context(purpose, description));
    }

    /**
     * Factory method alterno. Usado para reconstruir objetos desde persistencia.
     * - Llama a create(), lo cual valida.
     * - Lanza excepción si los datos no cumplen las reglas.
     */
    public static Context load(String purpose, String description) {
        return new Context(purpose, description);
    }

    /**
     * Valida el propósito según las reglas del dominio:
     * - Obligatorio
     * - No vacío
     * - Máximo de longitud
     */
    private static Result<String, DomainError> purposeValidation(String purpose, String fildName) {
        if (purpose == null || purpose.isBlank()) {
            return Validators.EMPTY_VALUE(fildName);
        }
        if (purpose.length() > MAX_PURPOSE_LENGTH) {
            return Validators.TOO_LONG(fildName, purpose, MAX_PURPOSE_LENGTH);
        }
        return Result.ok(purpose);
    }

    /**
     * Valida la descripción:
     * - Opcional
     * - Si existe, no debe exceder el máximo permitido
     */
    private static Result<String, DomainError> descriptionValidation(String description, String fildName) {
        if (description != null && !description.isBlank() && description.length() > MAX_DESCRIPTION_LENGTH) {
            return Validators.TOO_LONG(fildName, description, MAX_DESCRIPTION_LENGTH);
        }
        return Result.ok(description);
    }

    /** Devuelve el propósito (siempre no nulo y validado) */
    public String getPurpose() {
        return purpose;
    }

    /** Devuelve la descripción (puede ser nula o cadena vacía válida) */
    public String getDescription() {
        return description;
    }
}

