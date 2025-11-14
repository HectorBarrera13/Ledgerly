package toast.appback.src.shared.utils.result.chains;

public record ResultTuples() {
    protected record Tuple2<A, B>(A _1, B _2) {}
    protected record Tuple3<A, B, C>(A _1, B _2, C _3) {}
    protected record Tuple4<A, B, C, D>(A _1, B _2, C _3, D _4) {}
    protected record Tuple5<A, B, C, D, E>(A _1, B _2, C _3, D _4, E _5) {}
    protected record Tuple6<A, B, C, D, E, F>(A _1, B _2, C _3, D _4, E _5, F _6) {}
    protected record Tuple7<A, B, C, D, E, F, G>(A _1, B _2, C _3, D _4, E _5, F _6, G _7) {}
    protected record Tuple8<A, B, C, D, E, F, G, H>(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8) {}
}
