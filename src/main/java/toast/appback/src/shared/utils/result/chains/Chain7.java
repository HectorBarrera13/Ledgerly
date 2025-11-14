package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.HetaFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class Chain7<A, B, C, D, F, G, H, E extends IError> {
    private final Result<ResultTuples.Tuple7<A, B, C, D, F, G, H>, E> current;

    Chain7(Result<ResultTuples.Tuple7<A, B, C, D, F, G, H>, E> r) { this.current = r; }

    public <I> Chain8<A, B, C, D, F, G, H, I, E> and(Supplier<Result<I, E>> fn) {
        Result<I, E> r8 = fn.get();
        if (current.isSuccess() && r8.isSuccess()) {
            var t = current.getValue();
            return new Chain8<>(Result.success(new ResultTuples.Tuple8<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), r8.getValue())));
        } else {
            List<E> errors = new ArrayList<>();
            if (current.isFailure()) {
                errors.addAll(current.getErrors());
            }
            if (r8.isFailure()) {
                errors.addAll(r8.getErrors());
            }
            return new Chain8<>(Result.failure(errors));
        }
    }

    public <R> Result<R, E> result(HetaFunction<A, B, C, D, F, G, H, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7()));
        }
        return Result.failure(current.getErrors());
    }
}