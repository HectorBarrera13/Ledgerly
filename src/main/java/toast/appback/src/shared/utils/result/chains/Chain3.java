package toast.appback.src.shared.utils.result.chains;
import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.ResultAggregator;
import toast.appback.src.shared.utils.result.functions.TriFunction;

import java.util.function.Supplier;

public final class Chain3<A, B, C, E extends IError> {

    private final Result<ResultTuples.Tuple3<A, B, C>, E> current;

    Chain3(Result<ResultTuples.Tuple3<A, B, C>, E> r) {
        this.current = r;
    }

    public <D> Chain4<A, B, C, D, E> and(Supplier<Result<D, E>> fn) {
        Result<D, E> r4 = fn.get();
        if (current.isSuccess() && r4.isSuccess()) {
            var t = current.getValue();
            return new Chain4<>(Result.success(new ResultTuples.Tuple4<>(t._1(), t._2(), t._3(), r4.getValue())));
        } else {
            ResultAggregator<E> aggregator = Result.aggregator();
            if (current.isFailure()) {
                aggregator.add(current);
            }
            if (r4.isFailure()) {
                aggregator.add(r4);
            }
            return new Chain4<>(Result.failure(aggregator.getErrors()));
        }
    }

    public <R> Result<R, E> result(TriFunction<A, B, C, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3()));
        }
        return Result.failure(current.getErrors());
    }
}
