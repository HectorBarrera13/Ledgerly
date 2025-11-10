package toast.appback.src.middleware;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorData errorData;

    public ApplicationException(ErrorData errorData) {
        super(errorData.message());
        this.errorData = errorData;
    }

}
