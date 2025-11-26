package toast.appback.src.debts.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

public class Context {
    private static final String FIELD_PURPOSE = "purpose";
    private static final String FIELD_DESCRIPTION = "description";
    private static final int MAX_PURPOSE_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private final String purpose;
    private final String description;

    private Context(String purpose, String description) {
        this.purpose = purpose;
        this.description = description;
    }

    //Method use as a constructor for general cases
    public static Result<Context, DomainError> create(String purpose, String description) {
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(purposeValidation(purpose, FIELD_PURPOSE));
        emptyResult.collect(descriptionValidation(description, FIELD_DESCRIPTION));
        if (emptyResult.isFailure()) {
            return emptyResult.castFailure();
        }
        return Result.ok(new Context(purpose, description));
    }

    //Method use for quick creation, it does not validates
    public static Context load(String purpose, String description) {
        return create(purpose, description).orElseThrow(
                ()-> new IllegalArgumentException("invalid context data: " + purpose)
                );
    }

    private static Result<String, DomainError> purposeValidation(String purpose, String fildName) {
        if (purpose == null || purpose.isBlank()) {
            return Validators.EMPTY_VALUE(fildName);
        }
        if (purpose.length() > MAX_PURPOSE_LENGTH) {
            return Validators.TOO_LONG(fildName, purpose, MAX_PURPOSE_LENGTH);
        }
        return Result.ok(purpose);
    }

    private static Result<String, DomainError> descriptionValidation(String description, String fildName) {
        if (description != null && !description.isBlank() && description.length() > MAX_DESCRIPTION_LENGTH) {
            return Validators.TOO_LONG(fildName, description, MAX_DESCRIPTION_LENGTH);
        }
        return Result.ok(description);
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDescription() {
        return description;
    }

}
