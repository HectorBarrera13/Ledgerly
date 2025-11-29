package toast.appback.src.groups.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.shared.application.DomainException;

public class AddMemberException extends ApplicationException {
    public AddMemberException(String message) {
        super(message);
    }
}
