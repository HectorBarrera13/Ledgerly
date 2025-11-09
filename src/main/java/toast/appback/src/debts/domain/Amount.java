package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

public record Amount(String currency, Double quantity) {
    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_AMOUNT = "amount";
    private static final String REGEX_CURRENCY = "^[A-Z]{3}$";

    public static Result<Amount, DomainError> create(String currency, Double quantity) {
        return Result.combine(
                amountValidation(quantity, FIELD_AMOUNT),
                currencyValidation(currency,REGEX_CURRENCY, FIELD_CURRENCY)
        ).map(r->new Amount(currency, quantity));
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

    public static Result<String, DomainError> amountValidation(Double amount, String field) {
        if (amount == null) {
            return Validators.EMPTY_VALUE(field);
        }
        if(amount<0){
            return Validators.MUST_BE_POSITIVE(field,amount, "is negative");
        }
        return Result.success();
    }
}
