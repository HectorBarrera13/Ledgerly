package toast.appback.src.shared.utils.result;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A generic Result class that encapsulates the outcome of an operation, which can be either a success with a value of type T,
 * or a failure with a list of errors of type E.
 *
 * @param <T> the type of the successful result value
 * @param <E> the type of the error, which must implement IError
 *            <p> {@link IError} interface with message and error type.
 */
public class Result<T, E extends IError> {

    private final T value;

    private final boolean success;

    private final List<E> errors;

    protected Result(T value) {
        this.value = value;
        this.success = true;
        this.errors = Collections.emptyList();
    }

    protected Result(List<E> errors) {
        Objects.requireNonNull(errors, "Errors list must not be null for a failed Result.");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("Errors list cannot be empty for a failed Result. Use success() for no errors.");
        }
        this.value = null;
        this.success = false;
        this.errors = List.copyOf(errors);
    }

    protected Result(T value, List<E> errors) {
        this.value = value;
        this.errors = errors != null ? List.copyOf(errors) : Collections.emptyList();
        this.success = errors == null || errors.isEmpty();
    }

    public static <T, E extends IError> Result<T, E> of(T value, List<E> errors) {
        return new Result<>(value, errors);
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

    public static <E extends IError> ResultAggregator<E> aggregator() {
        return new ResultAggregator<>();
    }

    public static <T, E extends IError> Result<T, E> failure(E error) {
        Objects.requireNonNull(error, "Error must not be null.");
        return new Result<>(List.of(error));
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Result is a failure, no value present.");
        }
        return value;
    }

    private T getNullableValue() {
        return value;
    }

    public List<E> getErrors() {
        if (isSuccess()) {
            return Collections.emptyList();
        }
        return errors;
    }

    public void ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }
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

    public static <A, B, E extends IError> Result<Pair<A, B>, E> combine(Result<A, E> ra, Result<B, E> rb) {
        if (ra.isSuccess() && rb.isSuccess()) {
            return Result.success(new Pair<>(ra.getValue(), rb.getValue()));
        }

        List<E> errors = new ArrayList<>();
        if (ra.isFailure()) errors.addAll(ra.getErrors());
        if (rb.isFailure()) errors.addAll(rb.getErrors());

        return Result.failure(errors);
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

    public Result<Void, E> consume(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(this.value);
        }
        return Result.failure(this.errors);
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

    public <U> Result<U, E> captureFirstError(Function<? super T, Result<U, E>> mapper) {
        if (isSuccess()) {
            return mapper.apply(this.value);
        } else {
            return Result.failure(List.of(this.errors.getFirst()));
        }
    }

    public <U> Result<U, E> captureFirstError(Supplier<Result<U, E>> mapper) {
        if (isSuccess()) {
            return mapper.get();
        } else {
            return Result.failure(List.of(this.errors.getFirst()));
        }
    }

    /**
     * Transforms the successful value of this Result using the provided mapper function that returns another Result.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     * If both this Result and the Result returned by the mapper are failures, their errors are combined.
     *
     * @param mapper the function to transform the successful value into another Result
     * @param <U>    the type of the successful value in the resulting Result
     * @return a new Result containing the value from the Result returned by the mapper or the combined errors
     * @throws IllegalStateException if this Result is a failure and the mapper is applied
     */
    public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
        List<E> combinedErrors = new ArrayList<>(List.copyOf(this.errors)); // Start with current errors
        Result<U, E> result = mapper.apply(this.value); // Apply the mapper
        combinedErrors.addAll(result.getErrors()); // Combine errors from the mapper result
        return Result.of(result.getNullableValue(), combinedErrors); // Return a Result<U, E> with combined errors
    }

    /**
     * Transforms the successful value of this Result using the provided mapper function that returns another Result.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     * If both this Result and the Result returned by the mapper are failures, their errors are combined.
     * @param mapper the function to transform the successful value into another Result
     * @return a new Result containing the value from the Result returned by the mapper or the combined errors
     * @param <U>   the type of the successful value in the resulting Result
     */
    public <U> Result<U, E> flatMap(Supplier<Result<U, E>> mapper) {
        List<E> combinedErrors = new ArrayList<>(List.copyOf(this.errors)); // Start with current errors
        Result<U, E> result = mapper.get(); // Apply the mapper
        combinedErrors.addAll(result.getErrors()); // Combine errors from the mapper result
        return Result.of(result.getNullableValue(), combinedErrors); // Return a Result<Void, E> with combined errors
    }


    /**
     * Transforms the successful value of this Result using the provided mapper function that returns another Result.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     * If both this Result and the Result returned by the mapper are failures, their errors are combined.
     * Additionally, if the mapper throws a NullPointerException, it is caught and the current errors are returned.
     *
     * @param mapper the function to transform the successful value into another Result
     * @param <U>    the type of the successful value in the resulting Result
     * @return a new Result containing the value from the Result returned by the mapper or the combined errors
     */
    public <U> Result<U, E> safeFlatMap(Function<? super T, Result<U, E>> mapper) {
        List<E> combinedErrors = new ArrayList<>(List.copyOf(this.errors)); // Start with current errors
        Result<U, E> result;
        try {
            result = mapper.apply(this.value); // Apply the mapper
        } catch (NullPointerException e) { // Catch NPE from mapper
            return Result.of(null, combinedErrors); // Return a Result<U, E> with combined errors
        }
        combinedErrors.addAll(result.getErrors()); // Combine errors from the mapper result
        return Result.of(result.getNullableValue(), combinedErrors); // Return a Result<U, E> with combined errors
    }

    /**
     * Transforms the successful value of this Result using the provided mapper function that returns another Result.
     * If this Result is a failure, the errors are propagated without applying the mapper.
     * If both this Result and the Result returned by the mapper are failures, their errors are combined.
     * Additionally, if the mapper throws a NullPointerException, it is caught and the current errors are returned.
     *
     * @param mapper the supplier to transform the successful value into another Result
     * @param <U>    the type of the successful value in the resulting Result
     * @return a new Result containing the value from the Result returned by the mapper or the combined errors
     */
    public <U> Result<U, E> safeFlatMap(Supplier<Result<U, E>> mapper) {
        List<E> combinedErrors = new ArrayList<>(List.copyOf(this.errors)); // Start with current errors
        Result<U, E> result;
        try {
            result = mapper.get(); // Apply the mapper
        } catch (NullPointerException e) {
            return Result.of(null, combinedErrors); // Return a Result<U, E> with combined errors
        }
        combinedErrors.addAll(result.getErrors()); // Combine errors from the mapper result
        return Result.of(result.getNullableValue(), combinedErrors); // Return a Result<U, E> with combined errors
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
        return Result.of(null, combinedErrors); // Return a Result<Void, E> with combined errors
    }


    @Override
    public String toString() {
        return "Result{" +
                "value=" + value +
                ", success=" + success +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?, ?> result = (Result<?, ?>) o;
        return success == result.success && Objects.equals(value, result.value) && Objects.equals(errors, result.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, success, errors);
    }
}
