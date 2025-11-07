package toast.appback.src.shared;

public interface ResponseMapper<T, R> {
    R map(T value);
}
