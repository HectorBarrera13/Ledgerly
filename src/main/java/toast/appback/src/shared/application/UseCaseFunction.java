package toast.appback.src.shared.application;

import toast.appback.src.shared.utils.Result;

/**
 * Generic Use Case interface defining the contract for executing use cases.
 *
 * @param <R> The type of the {@link Result} returned by the use case.
 * @param <C> The type of command or input required to execute the use case.
 */
public interface UseCaseFunction<R, C> {
    R execute(C command);
}