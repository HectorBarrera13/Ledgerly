package toast.appback.src.groups.domain.vo;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

public class GroupInformation {
    private final String name;
    private final String description;
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 300;


    private GroupInformation(String name, String description) {
        this.name = name;
        this.description = description;
    }

     public static Result<GroupInformation, DomainError> create(String name, String description) {
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(nameValidation(name, FIELD_NAME));
        emptyResult.collect(descriptionValidation(description,FIELD_DESCRIPTION));
        if(emptyResult.isFailure()){
            return emptyResult.castFailure();
        }
        return Result.ok(new GroupInformation(name,description));
     }

     public static GroupInformation load(String name, String description) {
        return create(name, description).orElseThrow(
                ()-> new IllegalArgumentException("invalid group information: " + name));
     }

     private static Result<String, DomainError> nameValidation(String name, String fieldName) {
        if(name == null || name.isEmpty()){
            return Validators.EMPTY_VALUE(fieldName);
        }
        if(name.length() > 30){
            return Validators.TOO_LONG(fieldName,name,MAX_NAME_LENGTH);
        }
        return Result.ok(name);
     }

     private static Result<String, DomainError> descriptionValidation(String description, String fieldName) {
        if(description == null || description.isEmpty()){
            return Validators.EMPTY_VALUE(fieldName);
        }
        if(description.length() > 300){
            return Validators.TOO_LONG(fieldName,description,MAX_DESCRIPTION_LENGTH);
        }
        return Result.ok(description);
     }

     public String getName() {
        return name;
     }

     public String getDescription() {
        return description;
     }
}
