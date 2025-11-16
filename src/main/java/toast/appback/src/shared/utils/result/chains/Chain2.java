package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Chain2<A, B, E extends IError> {

    private final Result<ResultTuples.Tuple2<A, B>, E> current;

    Chain2(Result<ResultTuples.Tuple2<A, B>, E> r) {
        this.current = r;
    }

    public <C> Chain3<A, B, C, E> and(Supplier<Result<C, E>> fn) {
        Result<C, E> r3 = fn.get();
        if (current.isOk() && r3.isOk()) {
            var t = current.get();
            return new Chain3<>(Result.ok(new ResultTuples.Tuple3<>(t._1(), t._2(), r3.get())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r3.isFailure()) {
                emptyResult.collect(r3);
            }
            return new Chain3<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(BiFunction<A, B, R> fn) {
        if (current.isOk()) {
            var t = current.get();
            return Result.ok(fn.apply(t._1(), t._2()));
        }
        return Result.failure(current.getErrors());
    }
}

