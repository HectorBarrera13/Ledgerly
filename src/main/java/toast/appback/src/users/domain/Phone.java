package toast.appback.src.users.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.Validators;
import toast.appback.src.shared.errors.DomainError;

public record Phone(String countryCode, String number) {

    public static Result<Phone, DomainError> create(String countryCode, String number) {
        return isValidCode(countryCode)
                .flatMap(() -> isValidPhoneNumber(number)
                        .map(() -> new Phone(countryCode, number)));
    }

    private static Result<Void, DomainError> isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return Validators.EMPTY_VALUE("number");
        }
        if (!phoneNumber.matches("\\d{4,15}")) {
            return Validators.INVALID_FORMAT("number", phoneNumber, "Must contain only digits and be between 4 and 15 characters long");
        }
        return Result.success();
    }

    private static Result<Void, DomainError> isValidCode(String code) {
        if (code == null || code.isBlank()) {
            return Validators.EMPTY_VALUE("countryCode");
        }
        if (!code.matches("\\+\\d{1,4}")) {
            return Validators.INVALID_FORMAT("countryCode", code, "Must start with '+' followed by 1 to 4 digits");
        }
        return Result.success();
    }

    public String getValue() {
        return countryCode + "-" + number;
    }
}
