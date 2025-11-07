package toast.appback.src.middleware;

import lombok.Getter;

@Getter
public class ErrorHandler extends RuntimeException {

    private final ErrorData errorData;

    public ErrorHandler(ErrorData errorData) {
        super(errorData.message());
        this.errorData = errorData;
    }

}
