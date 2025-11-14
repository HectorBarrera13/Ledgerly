package toast.appback.src.shared.utils.result.chains;
import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.QuadFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record Chain4<A, B, C, D, E extends IError>(Result<ResultTuples.Tuple4<A, B, C, D>, E> current) {

    public <F> Chain5<A, B, C, D, F, E> and(Supplier<Result<F, E>> fn) {
        Result<F, E> r5 = fn.get();
        if (current.isSuccess() && r5.isSuccess()) {
            var t = current.getValue();
            return new Chain5<>(Result.success(new ResultTuples.Tuple5<>(t._1(), t._2(), t._3(), t._4(), r5.getValue())));
        } else {
            List<E> errors = new ArrayList<>();
            if (current.isFailure()) {
                errors.addAll(current.getErrors());
            }
            if (r5.isFailure()) {
                errors.addAll(r5.getErrors());
            }
            return new Chain5<>(Result.failure(errors));
        }
    }

    public <R> Result<R, E> result(QuadFunction<A, B, C, D, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3(), t._4()));
        }
        return Result.failure(current.getErrors());
    }
}
