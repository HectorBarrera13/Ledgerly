package toast.appback.src.shared.domain;

import toast.appback.src.shared.utils.result.Result;

/**
 * Utilidades de validación reutilizables que generan {@link Result} con {@link DomainError}.
 * <p>
 * Cada método devuelve un {@link Result} en fallo con información estructurada para reportar
 * validaciones de campos, colecciones y reglas específicas.
 */

public class Validators {
    private static final String VALUE_PREFIX = "value: '";
    private static final String VALUE_SUFFIX = "'";

    public static <T> Result<T, DomainError> emptyValue(String field) {
        return Result.failure(DomainError.validation(field, "value cannot be null or empty")
                .withValidatorType(ValidatorType.EMPTY_VALUE)
        );
    }

    public static <T> Result<T, DomainError> emptyCollection(String field, Iterable<?> value) {
        return Result.failure(DomainError.validation(field, "collection cannot be null or empty")
                .withDetails(VALUE_PREFIX + value + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.EMPTY_COLLECTION)
        );
    }

    public static <T> Result<T, DomainError> invalidState(String field, String desiredValue, String actualValue) {
        return Result.failure(DomainError.validation(field, "State" + actualValue + " cannot be converted to" + desiredValue)
                .withDetails("actual " + VALUE_PREFIX + actualValue + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.INVALID_STATE)
        );
    }

    public static <T> Result<T, DomainError> tooLong(String field, String value, int max) {
        return Result.failure(DomainError.validation(field, "value cannot be longer than " + max + " characters")
                .withDetails(VALUE_PREFIX + value + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.TOO_LONG)
        );
    }

    public static <T> Result<T, DomainError> tooShort(String field, String value, int min) {
        return Result.failure(DomainError.validation(field, "value cannot be shorter than " + min + " characters")
                .withDetails(VALUE_PREFIX + value + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.TOO_SHORT)
        );
    }

    public static <T> Result<T, DomainError> invalidFormat(String field, String value, String message) {
        return Result.failure(DomainError.validation(field, message)
                .withDetails(VALUE_PREFIX + value + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.INVALID_FORMAT)
        );
    }

    public static <T> Result<T, DomainError> mustBePositive(String field, Double value, String message) {
        return Result.failure(DomainError.validation(field, message)
                .withDetails(VALUE_PREFIX + value + VALUE_SUFFIX)
                .withValidatorType(ValidatorType.MUST_BE_POSITIVE)
        );
    }

    public static <T> Result<T, DomainError> mustNotContain(String field, String value, String substring) {
        return buildCharValidationMessage(field, value, substring, "value must not contain", ValidatorType.MUST_NOT_CONTAIN);
    }

    public static <T> Result<T, DomainError> mustNotStartWith(String field, String value, String prefix) {
        return buildCharValidationMessage(field, value, prefix, "value must not start with", ValidatorType.MUST_NOT_START_WITH);
    }

    public static <T> Result<T, DomainError> mustNotEndWith(String field, String value, String suffix) {
        return buildCharValidationMessage(field, value, suffix, "value must not end with", ValidatorType.MUST_NOT_END_WITH);
    }

    private static <T> Result<T, DomainError> buildCharValidationMessage(String field, String value, String prefix, String message, ValidatorType validatorType) {
        CharName charName = CharName.fromString(prefix); // Check if the prefix is a known special character
        return Result.failure(DomainError.validation(field, message + (charName != null ? " " + charName.getName() + " '" + charName.getCharacter() + "'" // Use the character name if it's a known special character
                : " '" + prefix + "'") // Otherwise, use the prefix as is
        ).withDetails(VALUE_PREFIX + value + VALUE_SUFFIX).withValidatorType(validatorType));
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
