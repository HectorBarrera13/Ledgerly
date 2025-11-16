package toast.appback.src.shared.utils.result.chains;
import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.QuadFunction;
import java.util.function.Supplier;

public record Chain4<A, B, C, D, E extends IError>(Result<ResultTuples.Tuple4<A, B, C, D>, E> current) {

    public <F> Chain5<A, B, C, D, F, E> and(Supplier<Result<F, E>> fn) {
        Result<F, E> r5 = fn.get();
        if (current.isOk() && r5.isOk()) {
            var t = current.get();
            return new Chain5<>(Result.ok(new ResultTuples.Tuple5<>(t._1(), t._2(), t._3(), t._4(), r5.get())));
        } else {
            Result<Void, E> emptyResult = Result.empty();
            if (current.isFailure()) {
                emptyResult.collect(current);
            }
            if (r5.isFailure()) {
                emptyResult.collect(r5);
            }
            return new Chain5<>(Result.failure(emptyResult.getErrors()));
        }
    }

    public <R> Result<R, E> result(QuadFunction<A, B, C, D, R> fn) {
        if (current.isOk()) {
            var t = current.get();
            return Result.ok(fn.apply(t._1(), t._2(), t._3(), t._4()));
        }
        return Result.failure(current.getErrors());
    }
}
