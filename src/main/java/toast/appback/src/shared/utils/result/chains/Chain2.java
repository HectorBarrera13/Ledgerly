package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.ResultAggregator;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Chain2<A, B, E extends IError> {

    private final Result<ResultTuples.Tuple2<A, B>, E> current;

    Chain2(Result<ResultTuples.Tuple2<A, B>, E> r) {
        this.current = r;
    }

    public <C> Chain3<A, B, C, E> and(Supplier<Result<C, E>> fn) {
        Result<C, E> r3 = fn.get();
        if (current.isSuccess() && r3.isSuccess()) {
            var t = current.getValue();
            return new Chain3<>(Result.success(new ResultTuples.Tuple3<>(t._1(), t._2(), r3.getValue())));
        } else {
            ResultAggregator<E> aggregator = Result.aggregator();
            if (current.isFailure()) {
                aggregator.add(current);
            }
            if (r3.isFailure()) {
                aggregator.add(r3);
            }
            return new Chain3<>(Result.failure(aggregator.getErrors()));
        }
    }

    public <R> Result<R, E> result(BiFunction<A, B, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2()));
        }
        return Result.failure(current.getErrors());
    }
}

