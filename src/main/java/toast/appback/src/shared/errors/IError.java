package toast.appback.src.shared.errors;

/**
 * Interface representing an error with an optional message and a type.
 * <p>{@link #message()} returns the error message as a string.
 * <p>{@link #details()} returns additional details about the error as a string.
 * <p>{@link #type()} returns the type of the error as an {@link ErrorTypeV}.
 * <p>{@link #field()} returns the field associated with the error as a string.
 */
public interface IError {
    String message();
    String details();
    ErrorTypeV type();
    String field();
}
