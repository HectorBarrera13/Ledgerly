package toast.appback.src.middleware;

import org.springframework.http.HttpStatus;
import toast.appback.src.shared.domain.DomainErrType;
import toast.appback.src.shared.errors.ErrorTypeV;
import toast.appback.src.shared.errors.IError;

import java.util.ArrayList;
import java.util.List;

public class ErrorsHandler {
    private ErrorsHandler() {
    }

    public static ErrorData handleSingleError(IError error) {
        return ErrorData.create(
                error.field(),
                error.message(),
                mapErrorToStatusCode(error.type())
        );
    }

    public static ErrorData handleMultipleErrors(List<? extends IError> errors) {
        List<ErrorDetails> details = new ArrayList<>();
        errors.forEach(err ->
                details.add(new ErrorDetails(err.field(), err.message()))
        );
        return ErrorData.create(
                "Multiple errors occurred",
                details
        );
    }

    public static ErrorData unknownError() {
        return ErrorData.create("An unexpected error occurred. Please try again later.");
    }

    private static HttpStatus mapErrorToStatusCode(ErrorTypeV errorType) {
        if (errorType instanceof DomainErrType domainErrType) {
            return mapDomainErrorToStatusCode(domainErrType);
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static HttpStatus mapDomainErrorToStatusCode(DomainErrType type) {
        return switch (type) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case BUSINESS_RULE_VIOLATION -> HttpStatus.FORBIDDEN;
            case UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
