package toast.appback.src.shared.utils.result;

import toast.appback.src.shared.errors.IError;

import java.util.ArrayList;
import java.util.List;

public class ResultAggregator<E extends IError> {

    protected ResultAggregator() {}

    private final List<E> errors = new ArrayList<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void add(Result<?, E> result) {
        result.ifFailure(errors::addAll);
    }

    public List<E> getErrors() {
        return errors;
    }

    public <T> Result<T, E> failureResult() {
        return Result.failure(errors);
    }
}
