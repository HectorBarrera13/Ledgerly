package toast.appback.src.shared.application;

import toast.appback.src.middleware.ApplicationException;

public interface UseCaseProcedure<C> {
    void execute(C command) throws ApplicationException;
}
