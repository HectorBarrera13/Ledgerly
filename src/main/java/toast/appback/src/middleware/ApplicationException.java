package toast.appback.src.middleware;

public class ApplicationException extends RuntimeException {

    private final ErrorData errorData;

    public ApplicationException(ErrorData errorData) {
        super(errorData.message());
        this.errorData = errorData;
    }

    public ErrorData getErrorData() {
        return errorData;
    }
}
