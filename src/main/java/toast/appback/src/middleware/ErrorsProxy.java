package toast.appback.src.middleware;

import org.springframework.http.HttpStatus;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ErrorsProxy {

    public static <T> void handleResult(Result<T, AppError> result) {
        if (result.isSuccess()) {
            return;
        }
        throw new ErrorHandler(handleError(result.getErrors()));
    }


    public static ErrorData handleError(List<AppError> error) {
        if (error.isEmpty()) {
            throw new IllegalArgumentException("Error list cannot be empty");
        }
        if (error.size() == 1) {
            if (error.getFirst() instanceof AppError appError) {
                return new ErrorData(
                        appError.field(),
                        appError.message(),
                        null,
                        Instant.now(),
                        mapAppErrorToStatusCode(appError.type())
                );
            }
        }
        List<ErrorDetails> details = new ArrayList<>();
        error.forEach(err ->
                details.add(new ErrorDetails(err.field(), err.message()))
        );
        return new ErrorData(
                "Multiple errors occurred",
                null,
                details,
                Instant.now(),
                HttpStatus.MULTI_STATUS.value()
        );
    }

    private static int mapAppErrorToStatusCode(AppErrType error) {
        return switch (error) {
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND.value();
            case DATA_INTEGRITY_VIOLATION -> HttpStatus.CONFLICT.value();
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST.value();
            case FORBIDDEN_OPERATION -> HttpStatus.FORBIDDEN.value();
            case AUTHORIZATION_FAILED, AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED.value();
            case EXTERNAL_SERVICE_ERROR -> HttpStatus.SERVICE_UNAVAILABLE.value();
            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR.value();
            case NOT_PROCESSABLE_ENTITY -> HttpStatus.UNPROCESSABLE_ENTITY.value();
        };
    }

}
