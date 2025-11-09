package toast.appback.src.shared;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.types.Result;

/**
 * Generic Use Case interface defining the contract for executing use cases.
 *
 * @param <R> The type of the {@link Result} returned by the use case.
 * @param <E> The type of error that can be returned by the use case, extending IError
 * @param <C> The type of command or input required to execute the use case.
 */
public interface UseCase<R, E extends IError, C> {
    Result<R, E> execute(C command);
}
