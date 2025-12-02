package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Value Object que representa el monto de una deuda junto con su moneda.
 * Encapsula reglas de validación:
 * - currency debe cumplir el formato ISO (3 letras mayúsculas)
 * - amount debe ser no nulo y positivo
 * - amount siempre se convierte a BigDecimal con SCALE = 2
 * <p>
 * Es completamente inmutable y propio del dominio.
 */
public class DebtMoney {

    // Constantes utilizadas para validación y reporte de errores
    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_AMOUNT = "amount";
    private static final String REGEX_CURRENCY = "^[A-Z]{3}$";
    // Escala utilizada al transformar la cantidad entera recibida
    private static final int SCALE = 2;
    // Monto expresado como BigDecimal con escala fija (2 decimales)
    private final BigDecimal amount;
    // Código ISO de la moneda (ej: "USD", "MXN")
    private final String currency;

    /**
     * Constructor privado. La creación debe ser controlada mediante create() o load().
     */
    private DebtMoney(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Factory method principal:
     * - Valida currency con regex (3 letras mayúsculas)
     * - Valida amount no nulo y positivo
     * - Convierte el monto Long a BigDecimal con escala de 2 decimales
     * - Devuelve Result para manejar errores de dominio sin excepciones
     */
    public static Result<DebtMoney, DomainError> create(String currency, Long amount) {
        Result<Void, DomainError> emptyResult = Result.empty();

        emptyResult.collect(currencyValidation(currency, REGEX_CURRENCY, FIELD_CURRENCY));
        emptyResult.collect(amountValidation(amount, FIELD_AMOUNT));

        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }

        return Result.ok(new DebtMoney(amountTransformation(amount, SCALE), currency));
    }

    /**
     * Factory method alternativo para reconstrucción desde persistencia.
     * No valida reglas porque se asume que ya fueron validadas antes.
     */
    public static DebtMoney load(BigDecimal amount, String currency) {
        return new DebtMoney(amount, currency);
    }

    /**
     * Validación del código de moneda.
     */
    private static Result<String, DomainError> currencyValidation(String currency, String format, String field) {
        if (currency == null) {
            return Validators.emptyValue(field);
        }
        if (!currency.matches(format)) {
            return Validators.invalidFormat(field, currency, "deben ser 3 letras");
        }
        return Result.ok();
    }

    /**
     * Validación del monto de la deuda.
     */
    private static Result<String, DomainError> amountValidation(Long amount, String field) {
        if (amount == null) {
            return Validators.emptyValue(field);
        }
        if (amount < 0) {
            return Validators.mustBePositive(field, amount.doubleValue(), "MUST_BE_POSITIVE");
        }
        return Result.ok();
    }

    /**
     * Convierte el monto entero (Long) a BigDecimal escalado a 2 decimales.
     * Ej: 1000 => 10.00
     */
    private static BigDecimal amountTransformation(Long amount, int scale) {
        BigInteger unscaledAmount = BigInteger.valueOf(amount);
        return new BigDecimal(unscaledAmount, scale);
    }

    /**
     * Implementación de igualdad basada en valor, siguiendo las reglas de un Value Object.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DebtMoney debtMoney = (DebtMoney) o;
        return Objects.equals(amount, debtMoney.amount) &&
                Objects.equals(currency, debtMoney.currency);
    }

    /**
     * hashCode consistente con equals.
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(amount);
        result = 31 * result + Objects.hashCode(currency);
        return result;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}