package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.Result;

public class Context {
    private final String purpose;
    private final String description;

    private static final String FIELD_PURPOSE = "purpose";
    private static final String FIELD_DESCRIPTION = "description";
    private static final int maxPurposeLength = 30;
    private static final int maxDescriptionLength = 200;

    private Context(String purpose, String description) {
        this.purpose = purpose;
        this.description = description;
    }
    //Method use as a constructor for general cases
    public static Result<Context, DomainError> create(String purpose, String description) {
        return Result.combine(
                purposeValidation(purpose,FIELD_PURPOSE),
                descriptionValidation(description,FIELD_DESCRIPTION)
        ).map(r -> new Context(purpose,description));
    }

    //Method use for quick creation, it does not validates
    public static Context load(String purpose, String description) {
        return new Context(purpose,description);
    }

    public static Result<String, DomainError> purposeValidation(String purpose, String fildName) {
        if(purpose.isBlank()){
            return Validators.EMPTY_VALUE(fildName);
        }
        if(purpose.length()>maxPurposeLength){
            return Validators.TOO_LONG(fildName, purpose, maxPurposeLength);
        }
        return Result.success(purpose);
    }

    public static Result<String, DomainError> descriptionValidation(String description, String fildName) {
        if(description.length()>maxDescriptionLength){
            return Validators.TOO_LONG(fildName, description, maxDescriptionLength);
        }
        return Result.success(description);
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDescription() {
        return description;
    }

}
