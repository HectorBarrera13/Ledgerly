package toast.appback.src.shared.application;

public interface UseCaseProcedure<C> {
    void execute(C command);
}
