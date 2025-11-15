package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.PentaFunction;

import java.util.function.Supplier;

public final class Chain5<A, B, C, D, F, E extends IError> {
    private final Result<ResultTuples.Tuple5<A, B, C, D, F>, E> current;

    Chain5(Result<ResultTuples.Tuple5<A, B, C, D, F>, E> r) { this.current = r; }

    public <G> Chain6<A, B, C, D, F, G, E> and(Supplier<Result<G, E>> fn) {
        Result<G, E> r6 = fn.get();
        if (current.isSuccess() && r6.isSuccess()) {
            var t = current.getValue();
            return new Chain6<>(Result.success(new ResultTuples.Tuple6<>(t._1(), t._2(), t._3(), t._4(), t._5(), r6.getValue())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r6.isFailure()) {
                emptyResult.collect(r6);
            }
            return new Chain6<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(PentaFunction<A, B, C, D, F, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5()));
        }
        return Result.failure(current.getErrors());
    }
}
