package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.Result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class DebtMoney {
    private final BigDecimal amount;
    private final String currency;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DebtMoney debtMoney = (DebtMoney) o;
        return Objects.equals(amount, debtMoney.amount) && Objects.equals(currency, debtMoney.currency);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(amount);
        result = 31 * result + Objects.hashCode(currency);
        return result;
    }

    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_AMOUNT = "amount";
    private static final String REGEX_CURRENCY = "^[A-Z]{3}$";
    private static final int SCALE = 2;

    private DebtMoney(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Result<DebtMoney, DomainError> create(String currency, Long amount, String UNIT_SCALE) {
        return Result.combine(
                currencyValidation(currency,REGEX_CURRENCY, FIELD_CURRENCY),
                amountValidation(amount, FIELD_AMOUNT)
        ).map(r->new DebtMoney(amountTransformation(amount, SCALE), currency));
    }

    public static DebtMoney load(Long amount, String currency) {
        BigDecimal amountTransformed = amountTransformation(amount, SCALE);
        return new DebtMoney(amountTransformed, currency);
    }

    public static Result<String, DomainError> currencyValidation(String currency, String format, String field) {
        if (currency == null) {
            return Validators.EMPTY_VALUE(field);
        }
        if(!currency.matches(format)) {
            return Validators.INVALID_FORMAT(field,currency, "deben ser 3 letras");
        }
        return Result.success();
    }

    public static Result<String, DomainError> amountValidation(Long amount, String field) {
        if (amount == null) {
            return Validators.EMPTY_VALUE(field);
        }
        if(amount<0){
            return Validators.MUST_BE_POSITIVE(field,amount.doubleValue(), "MUST_BE_POSITIVE");
        }
        return Result.success();
    }

    private static BigDecimal amountTransformation(Long amount, int scale) {
        BigInteger unscaledAmount = BigInteger.valueOf(amount);
        return new BigDecimal(unscaledAmount, scale);
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
