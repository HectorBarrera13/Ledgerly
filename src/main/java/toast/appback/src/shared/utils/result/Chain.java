package toast.appback.src.shared.utils.result;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.chains.Chain1;

import java.util.function.Supplier;

public class Chain {
    private Chain() {}

    protected static <E extends IError> Chain0<E> start() {
        return new Chain0<>();
    }

    public static final class Chain0<E extends IError> {
        private Chain0() {}

        public <A> Chain1<A, E> and(Supplier<Result<A, E>> fn) {
            return Chain1.start(fn.get());
        }
    }
}
