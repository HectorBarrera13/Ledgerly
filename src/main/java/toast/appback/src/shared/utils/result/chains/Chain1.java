package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Chain1<A, E extends IError> {
    private final Result<A, E> current;

    private Chain1(Result<A, E> current) {
        this.current = current;
    }

    public static <A, E extends IError> Chain1<A, E> start(Result<A, E> r1) {
        return new Chain1<>(r1);
    }

    public <B> Chain2<A, B, E> and(Supplier<Result<B, E>> fn) {
        Result<B, E> r2 = fn.get();
        if (current.isOk() && r2.isOk()) {
            return new Chain2<>(Result.ok(new ResultTuples.Tuple2<>(current.get(), r2.get())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r2.isFailure()) {
                emptyResult.collect(r2);
            }
            return new Chain2<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(Function<A, R> fn) {
        return current.isOk() ? Result.ok(fn.apply(current.get())) : Result.failure(current.getErrors());
    }
}
