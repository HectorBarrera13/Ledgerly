package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.util.Objects;

/**
 * Objeto de valor que representa un número de teléfono internacional dividido en código de país y número.
 *
 * <p>Reglas de validación principales:
 * - `countryCode` debe comenzar con '+' seguido de 1 a 4 dígitos.
 * - `number` debe contener solo dígitos y tener entre 4 y 15 caracteres.
 */
public class Phone {

    private final String countryCode;
    private final String number;

    private Phone(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }

    /**
     * Intenta construir un {@link Phone} validado.
     *
     * @param countryCode Código de país (ejemplo: "+34").
     * @param number      Número de teléfono (solo dígitos).
     * @return Resultado con la instancia `Phone` o un `DomainError` si falla la validación.
     */
    public static Result<Phone, DomainError> create(String countryCode, String number) {
        Result<Void, DomainError> result = Result.empty();
        result.collect(isValidCode(countryCode));
        result.collect(isValidPhoneNumber(number));
        if (result.isFailure()) {
            return result.castFailure();
        }
        return Result.ok(new Phone(countryCode, number));
    }

    /**
     * Carga un {@link Phone} asumiendo que los valores son válidos. Lanza {@link IllegalArgumentException}
     * si la creación falla.
     *
     * @param countryCode Código de país.
     * @param number      Número de teléfono.
     * @return Instancia `Phone` válida.
     * @throws IllegalArgumentException Si los valores no cumplen las validaciones.
     */
    public static Phone load(String countryCode, String number) {
        return create(countryCode, number)
                .orElseThrow(() -> new IllegalArgumentException("invalid phone data: " + countryCode + ", " + number));
    }

    private static Result<Void, DomainError> isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return Validators.emptyValue("number");
        }
        if (!phoneNumber.matches("\\d{4,15}")) {
            return Validators.invalidFormat("number", phoneNumber, "must contain only digits and be between 4 and 15 characters long");
        }
        return Result.ok();
    }

    private static Result<Void, DomainError> isValidCode(String code) {
        if (code == null || code.isBlank()) {
            return Validators.emptyValue("phoneCountryCode");
        }
        if (!code.matches("\\+\\d{1,4}")) {
            return Validators.invalidFormat("phoneCountryCode", code, "must start with '+' followed by 1 to 4 digits");
        }
        return Result.ok();
    }

    /**
     * @return Código de país, por ejemplo "+34".
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @return Número de teléfono (solo dígitos).
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return Representación combinada del teléfono: "{countryCode}-{number}".
     */
    public String getValue() {
        return countryCode + "-" + number;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "phoneCountryCode='" + countryCode + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Phone phone)) return false;
        return Objects.equals(countryCode, phone.countryCode) && Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }
}
