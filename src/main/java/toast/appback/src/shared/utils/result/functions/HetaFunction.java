package toast.appback.src.shared.utils.result.functions;

@FunctionalInterface
public interface HetaFunction<A, B, C, D, E, F, G, R> {
    R apply(A a, B b, C c, D d, E e, F f, G g);
}
