package toast.appback.src.shared.utils.result;

import toast.appback.src.shared.errors.IError;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A generic Result class that encapsulates the outcome of an operation, which can be either a success with a value of type T,
 * or a failure with a list of errors of type E.
 * <p> {@link IError} interface with message and error type.
 * @param <T> the type of the successful result value
 * @param <E> the type of the error, which must implement IError
 */
public class Result<T, E extends IError> {

    private final T value;

    private final List<E> errors = new ArrayList<>();

    protected Result(T value) {
        this.value = value;
    }

    protected Result(List<E> errors) {
        Objects.requireNonNull(errors, "Errors list must not be null for a failed Result.");
        this.value = null;
        this.errors.addAll(errors);
    }

    protected Result(T value, List<E> errors) {
        Objects.requireNonNull(errors, "Errors list must not be null.");
        this.value = value;
        this.errors.addAll(errors);
    }

    public static <T, E extends IError> Result<T, E> empty() {
        return new Result<>(null, List.of());
    }

    public static <T, E extends IError> Result<T, E> success(T value) {
        return new Result<>(value);
    }

    public static <T, E extends IError> Result<T, E> success() {
        return new Result<>(null, List.of());
    }

    public static <T, E extends IError> Result<T, E> failure(List<E> errors) {
        return new Result<>(errors);
    }

    public static <E extends IError> Chain.Chain0<E> chain() {
        return Chain.start();
    }

    public static <T, E extends IError> Result<T, E> failure(E error) {
        Objects.requireNonNull(error, "Error must not be null.");
        return new Result<>(List.of(error));
    }

    public boolean isSuccess() {
        return this.errors.isEmpty();
    }

    public boolean isFailure() {
        return !this.isSuccess();
    }

    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Result is a failure, no value present.");
        }
        return value;
    }

    public List<E> getErrors() {
        if (isSuccess()) {
            return Collections.emptyList();
        }
        return errors;
    }

    public void ifFailure(Consumer<List<E>> consumer) {
        if (isFailure()) {
            consumer.accept(errors);
        }
    }

    public <X extends Throwable> void ifFailureThrows(Function<List<E>, ? extends X> exceptionFunction) throws X {
        if (isFailure()) {
            throw exceptionFunction.apply(errors);
        }
    }

    public <X extends Throwable> T orElseThrow(Function<List<E>, ? extends X> exceptionFunction) throws X {
        if (isFailure()) {
            throw exceptionFunction.apply(errors);
        }
        return value;
    }

    public <U> void collect(Result<U, E> other) {
        if (other.isFailure()) {
            this.errors.addAll(other.getErrors());
        }
    }

    public <U> Result<U, E> castFailure() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot cast a successful Result to a failure.");
        }
        return Result.failure(this.errors);
    }


    /**
     * Transforms the successful value of this Result using the provided mapper function.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     *
     * @param mapper the function to transform the successful value
     * @return a new Result containing the transformed value or the original errors
     * @throws IllegalStateException if this Result is a failure and the mapper is applied
     */
    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(this.value));
        } else {
            return Result.failure(this.errors);
        }
    }

    /**
     * Transforms the successful value of this Result using the provided mapper function.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     *
     * @param mapper the supplier to transform the successful value
     * @return a new Result containing the transformed value or the original errors
     * @throws IllegalStateException if this Result is a failure and the mapper is applied
     */
    public <U> Result<U, E> map(Supplier<U> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.get());
        } else {
            return Result.failure(this.errors);
        }
    }

    /**
     * Chains another operation that returns a Result<Void, E> to be executed if this Result is successful.
     * If this Result is a failure, the errors are propagated without executing the operation.
     * If both this Result and the Result returned by the operation are failures, their errors are combined.
     *
     * @param operation the operation to be executed if this Result is successful
     * @return a new {@code Result<Void, E>} containing no value but the combined errors if any
     * @throws IllegalStateException if this Result is a failure and the operation is executed
     */
    public Result<Void, E> andThen(Supplier<Result<Void, E>> operation) {
        List<E> combinedErrors = new ArrayList<>(this.errors); // Start with current errors
        Result<Void, E> result = operation.get(); // Execute the operation
        combinedErrors.addAll(result.getErrors()); // Combine errors from the operation
        return new Result<>(null, combinedErrors);
    }

    @Override
    public String toString() {
        return "Result{" +
                "value=" + value +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Result<?, ?> result)) return false;
        return Objects.equals(value, result.value) && Objects.equals(errors, result.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, errors);
    }
}
