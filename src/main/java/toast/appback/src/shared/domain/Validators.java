package toast.appback.src.shared.domain;

import toast.appback.src.shared.utils.Result;

/**
 * A utility class providing static methods for generating standardized validation error results.
 * Each method returns a Result object encapsulating a DomainError with a specific validation message.
 * The class includes an enumeration for common special characters to enhance error message clarity.
 * Example usage:
 * <pre>
 *     Result<String, DomainError> result = Validators.TOO_LONG("username", "verylongusername", 10);
 *     if (result.isFailure()) {
 *         DomainError error = result.getFailure();
 *         // Handle the validation error
 *     }
 * </pre>
 */

public class Validators {
    public static <T> Result<T, DomainError> EMPTY_VALUE(String field) {
        return Result.failure(DomainError.validation(field, "value cannot be null or empty"));
    }

    public static <T> Result<T, DomainError> EMPTY_COLLECTION(String field, Iterable<?> value) {
        return Result.failure(DomainError.validation(field, "collection cannot be null or empty")
                .withDetails("value: '" + value + "'")
                .withValidatorType(ValidatorType.EMPTY_COLLECTION)
        );
    }

    public static <T> Result<T, DomainError> INVAlID_STATE(String field, String desiredValue, String actualValue) {
        return Result.failure(DomainError.validation(field, "State" + actualValue + " cannot be converted to" + desiredValue)
                .withDetails("actual value: '" + actualValue + "'")
                .withValidatorType(ValidatorType.INVALID_STATE)
        );
    }

    public static <T> Result<T, DomainError> TOO_LONG(String field, String value, int max) {
        return Result.failure(DomainError.validation(field, "value cannot be longer than " + max + " characters")
                .withDetails("value: '" + value + "'")
                .withValidatorType(ValidatorType.TOO_LONG)
        );
    }

    public static <T> Result<T, DomainError> TOO_SHORT(String field, String value, int min) {
        return Result.failure(DomainError.validation(field, "value cannot be shorter than " + min + " characters")
                .withDetails("value: '" + value + "'")
                .withValidatorType(ValidatorType.TOO_SHORT)
        );
    }

    public static <T> Result<T, DomainError> INVALID_FORMAT(String field, String value, String message) {
        return Result.failure(DomainError.validation(field, message)
                .withDetails("value: '" + value + "'")
                .withValidatorType(ValidatorType.INVALID_FORMAT)
        );
    }

    public static <T> Result<T, DomainError> MUST_BE_POSITIVE(String field, Double value, String message) {
        return Result.failure(DomainError.validation(field, message)
                .withDetails("Value: '" + value + "'")
                .withValidatorType(ValidatorType.MUST_BE_POSITIVE)
        );
    }

    public static <T> Result<T, DomainError> MUST_NOT_CONTAIN(String field, String value, String substring) {
        return buildCharValidationMessage(field, value, substring, "value must not contain", ValidatorType.MUST_NOT_CONTAIN);
    }

    public static <T> Result<T, DomainError> MUST_NOT_START_WITH(String field, String value, String prefix) {
        return buildCharValidationMessage(field, value, prefix, "value must not start with", ValidatorType.MUST_NOT_START_WITH);
    }

    public static <T> Result<T, DomainError> MUST_NOT_END_WITH(String field, String value, String suffix) {
        return buildCharValidationMessage(field, value, suffix, "value must not end with", ValidatorType.MUST_NOT_END_WITH);
    }

    private static <T> Result<T, DomainError> buildCharValidationMessage(String field, String value, String prefix, String message, ValidatorType validatorType) {
        CharName charName = CharName.fromString(prefix); // Check if the prefix is a known special character
        return Result.failure(DomainError.validation(field, message + (charName != null ? " " + charName.getName() + " '" + charName.getCharacter() + "'" // Use the character name if it's a known special character
                : " '" + prefix + "'") // Otherwise, use the prefix as is
        ).withDetails("value: '" + value + "'").withValidatorType(validatorType));
    }


    private enum CharName {
        DOT(".", "dot"),
        UNDERSCORE("_", "underscore"),
        DASH("-", "dash"), AT("@", "at"),
        SLASH("/", "slash"), BACKSLASH("\\", "backslash"),
        COLON(":", "colon"), SEMICOLON(";", "semicolon"),
        COMMA(",", "comma"), SPACE(" ", "space"),
        EQUALS("=", "equals"), PLUS("+", "plus"),
        QUESTION_MARK("?", "question mark"), AMPERSAND("&", "ampersand");

        private final String character;
        private final String name;

        CharName(String character, String name) {
            this.character = character;
            this.name = name;
        }

        public static CharName fromString(String value) {
            for (CharName charName : values()) {
                if (charName.getCharacter().equals(value)) {
                    return charName;
                }
            }
            return null;
        }

        public String getCharacter() {
            return character;
        }

        public String getName() {
            return name;
        }
    }
}

