package toast.appback.src.middleware;

//import org.springframework.http.HttpStatus;
//import toast.appback.src.shared.application.AppErrType;
//import toast.appback.src.shared.domain.DomainErrType;
//import toast.appback.src.shared.errors.ErrorTypeV;
//import toast.appback.src.shared.errors.IError;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;

public class ErrorsHandler {

//    private static ErrorData handleMultipleErrors(List<? extends IError> errors) {
//        List<ErrorDetails> details = new ArrayList<>();
//        errors.forEach(err ->
//                details.add(new ErrorDetails(err.field(), err.message()))
//        );
//        return new ErrorData(
//                null,
//                "Multiple errors occurred",
//                details,
//                Instant.now(),
//                HttpStatus.MULTI_STATUS.value()
//        );
//    }
//
//    private static int mapErrorToStatusCode(ErrorTypeV errorType) {
//        if (errorType instanceof DomainErrType domainErrType) {
//            return mapDomainErrorToStatusCode(domainErrType);
//        } else if (errorType instanceof AppErrType domainError) {
//            return mapAppErrorToStatusCode(domainError);
//        } else {
//            throw new IllegalArgumentException("Unknown error type");
//        }
//    }
//
//    private static int mapAppErrorToStatusCode(AppErrType type) {
//        return switch (type) {
//            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND.value();
//            case DATA_INTEGRITY_VIOLATION -> HttpStatus.CONFLICT.value();
//            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST.value();
//            case FORBIDDEN_OPERATION -> HttpStatus.FORBIDDEN.value();
//            case AUTHORIZATION_FAILED, AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED.value();
//            case EXTERNAL_SERVICE_ERROR -> HttpStatus.SERVICE_UNAVAILABLE.value();
//            case UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR.value();
//            case NOT_PROCESSABLE_ENTITY -> HttpStatus.UNPROCESSABLE_ENTITY.value();
//        };
//    }
//
//    private static int mapDomainErrorToStatusCode(DomainErrType type) {
//        return switch (type) {
//            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST.value();
//            case BUSINESS_RULE_VIOLATION -> HttpStatus.FORBIDDEN.value();
//            case UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR.value();
//        };
//    }
}
