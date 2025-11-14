package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.HexFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class Chain6<A, B, C, D, F, G, E extends IError> {
    private final Result<ResultTuples.Tuple6<A, B, C, D, F, G>, E> current;

    Chain6(Result<ResultTuples.Tuple6<A, B, C, D, F, G>, E> r) { this.current = r; }

    public <H> Chain7<A, B, C, D, F, G, H, E> and(Supplier<Result<H, E>> fn) {
        Result<H, E> r7 = fn.get();
        if (current.isSuccess() && r7.isSuccess()) {
            var t = current.getValue();
            return new Chain7<>(Result.success(new ResultTuples.Tuple7<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), r7.getValue())));
        } else {
            List<E> errors = new ArrayList<>();
            if (current.isFailure()) {
                errors.addAll(current.getErrors());
            }
            if (r7.isFailure()) {
                errors.addAll(r7.getErrors());
            }
            return new Chain7<>(Result.failure(errors));
        }
    }

    public <R> Result<R, E> result(HexFunction<A, B, C, D, F, G, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5(), t._6()));
        }
        return Result.failure(current.getErrors());
    }
}