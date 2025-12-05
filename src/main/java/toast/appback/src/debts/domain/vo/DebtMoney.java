package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Value Object que representa el monto de una deuda junto con su moneda.
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
     * Valida las reglas de negocio antes de crear una instancia.
     *
     * @param currency Código ISO de 3 letras.
     * @param amount   Monto en centavos (Long); se transforma a BigDecimal con escala fija.
     * @return Result con instancia válida o DomainError con detalles de validación.
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
     */
    public static DebtMoney load(BigDecimal amount, String currency) {
        return new DebtMoney(amount, currency);
    }


    private static Result<String, DomainError> currencyValidation(String currency, String format, String field) {
        if (currency == null) {
            return Validators.emptyValue(field);
        }
        if (!currency.matches(format)) {
            return Validators.invalidFormat(field, currency, "deben ser 3 letras");
        }
        return Result.ok();
    }

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
     * @return Código ISO de la moneda.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @return Monto como BigDecimal con escala fija.
     */
    public BigDecimal getAmount() {
        return amount;
    }
}