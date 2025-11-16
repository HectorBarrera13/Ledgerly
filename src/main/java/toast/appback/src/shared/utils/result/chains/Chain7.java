package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.HetaFunction;

import java.util.function.Supplier;

public final class Chain7<A, B, C, D, F, G, H, E extends IError> {
    private final Result<ResultTuples.Tuple7<A, B, C, D, F, G, H>, E> current;

    Chain7(Result<ResultTuples.Tuple7<A, B, C, D, F, G, H>, E> r) { this.current = r; }

    public <I> Chain8<A, B, C, D, F, G, H, I, E> and(Supplier<Result<I, E>> fn) {
        Result<I, E> r8 = fn.get();
        if (current.isOk() && r8.isOk()) {
            var t = current.get();
            return new Chain8<>(Result.ok(new ResultTuples.Tuple8<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), r8.get())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r8.isFailure()) {
                emptyResult.collect(r8);
            }
            return new Chain8<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(HetaFunction<A, B, C, D, F, G, H, R> fn) {
        if (current.isOk()) {
            var t = current.get();
            return Result.ok(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7()));
        }
        return Result.failure(current.getErrors());
    }
}