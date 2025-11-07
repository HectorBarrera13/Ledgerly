package toast.appback.src.shared.errors;

/**
 * Interface representing an error with an optional message and a type.
 * <p>{@link #message()} returns the error message as a string.
 * <p>{@link #type()} returns the type of the error as an {@link ErrorTypeV}.
 */
public interface IError {
    String message();

    ErrorTypeV type();
}
