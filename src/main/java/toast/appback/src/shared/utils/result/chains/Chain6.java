package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.HexFunction;

import java.util.function.Supplier;

public final class Chain6<A, B, C, D, F, G, E extends IError> {
    private final Result<ResultTuples.Tuple6<A, B, C, D, F, G>, E> current;

    Chain6(Result<ResultTuples.Tuple6<A, B, C, D, F, G>, E> r) { this.current = r; }

    public <H> Chain7<A, B, C, D, F, G, H, E> and(Supplier<Result<H, E>> fn) {
        Result<H, E> r7 = fn.get();
        if (current.isOk() && r7.isOk()) {
            var t = current.get();
            return new Chain7<>(Result.ok(new ResultTuples.Tuple7<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), r7.get())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r7.isFailure()) {
                emptyResult.collect(r7);
            }
            return new Chain7<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(HexFunction<A, B, C, D, F, G, R> fn) {
        if (current.isOk()) {
            var t = current.get();
            return Result.ok(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5(), t._6()));
        }
        return Result.failure(current.getErrors());
    }
}