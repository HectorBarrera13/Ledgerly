package toast.appback.src.shared;

import toast.appback.src.shared.domain.BusinessCode;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueObjectsUtils {
    public static void assertErrorExists(List<DomainError> errors, ValidatorType validatorType) {
        boolean found = errors.stream()
                .anyMatch(error -> error.validatorType().equals(validatorType));
        assertTrue(found);
    }

    public static void assertErrorExistsForField(List<DomainError> errors, ValidatorType validatorType, String field) {
        boolean found = errors.stream()
                .anyMatch(error -> error.validatorType().equals(validatorType) && error.field().equals(field));
        assertTrue(found);
    }

    public static void assertOnlyErrorExists(List<DomainError> errors, ValidatorType validatorType) {
        long count = errors.stream()
                .filter(error -> error.validatorType().equals(validatorType))
                .count();
        assertEquals(1, count);
    }

    public static void assertOnlyErrorExistsForField(List<DomainError> errors, ValidatorType validatorType, String field) {
        long count = errors.stream()
                .filter(error -> error.validatorType().equals(validatorType) && error.field().equals(field))
                .count();
        assertEquals(1, count);
    }

    public static <T extends BusinessCode> void assertBusinessRuleErrorExists(List<DomainError> errors, T businessCode) {
        boolean found = errors.stream()
                .anyMatch(error -> error.businessCode().equals(businessCode));
        assertTrue(found);
    }
}
