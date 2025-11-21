package toast.appback.src.debts.domain.vo;

import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Role {
    private static final String[] validRoles = {"DEBTOR", "CREDITOR"};
    private static final String ROLE_FIELD = "role";
    public static final Role CREDITOR = new Role("CREDITOR");
    public static final Role DEBTOR = new Role("DEBTOR");
    private final String role;

    private Role(String role) {
        this.role = role;
    }

    public static Result<Role, DomainError> create(String role) {
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(validateRol(role, ROLE_FIELD));
        if(emptyResult.isFailure()){
            return emptyResult.castFailure();
        }
        return Result.ok(new Role(role));
    }

    public static Role load(String role) {
        return new Role(role);
    }
    public static Result<String, DomainError> validateRol(String role, String field) {
        if (role.isBlank() || role.isEmpty()){
            return Validators.EMPTY_VALUE(field);
        }
        for (String validRole : validRoles) {
            if (role.equalsIgnoreCase(validRole)) {
                return Result.ok();
            }
        }
        return Validators.INVALID_FORMAT(field, role, "Must be either DEBTOR or CREDITOR");
    }

    public String getRole() {
        return role;
    }
}
