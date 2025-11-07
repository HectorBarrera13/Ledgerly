package toast.appback.src.shared.types;

public record Pair<A, B>(A first, B second) {
    public static <A, B, C> Pair<Pair<A, B>, C> of(Pair<A, B> first, C second) {
        return new Pair<>(first, second);
    }
}
