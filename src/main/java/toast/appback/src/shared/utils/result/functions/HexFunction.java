package toast.appback.src.shared.utils.result.functions;

@FunctionalInterface
public interface HexFunction<A, B, C, D, E, F, R> {
    R apply(A a, B b, C c, D d, E e, F f);
}
