package toast.appback.src.shared.utils.result.chains;

import toast.appback.src.shared.errors.IError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.utils.result.functions.OctaFunction;

public class Chain8<A, B, C, D, F, G, H, I, E extends IError> {
    private final Result<ResultTuples.Tuple8<A, B, C, D, F, G, H, I>, E> current;

    Chain8(Result<ResultTuples.Tuple8<A, B, C, D, F, G, H, I>, E> r) { this.current = r; }

    public <R> Result<R, E> result(OctaFunction<A, B, C, D, F, G, H, I, R> fn) {
        if (current.isSuccess()) {
            var t = current.getValue();
            return Result.success(fn.apply(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), t._8()));
        }
        return Result.failure(current.getErrors());
    }
}
