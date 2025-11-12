package toast.appback.src.shared;

import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorMapperTest {

    @Test
    public void errorMapperWithProcedures() {
        Result<Void, DomainError> firstFailure = Result.failure(DomainError.businessRule("first"));
        Result<Void, DomainError> secondFailure = Result.failure(DomainError.businessRule("second"));
        Result<Void, DomainError> thirdFailure = Result.failure(DomainError.businessRule("third"));

        Result<Void, DomainError> mappedResult = firstFailure
                .andThen(() -> secondFailure)
                .andThen(() -> thirdFailure);
        assertTrue(mappedResult.isFailure());
        assertEquals("first", mappedResult.getErrors().getFirst().message());
        assertEquals("second", mappedResult.getErrors().get(1).message());

        assertEquals(3, mappedResult.getErrors().size());
    }

    @Test
    public void successProcedures() {
        Result<Void, DomainError> firstSuccess = Result.success();
        Result<Void, DomainError> secondSuccess = Result.success();
        Result<Void, DomainError> thirdSuccess = Result.success();

        Result<Void, DomainError> mappedResult = firstSuccess
                .andThen(() -> secondSuccess)
                .andThen(() -> thirdSuccess);
        assertTrue(mappedResult.isSuccess());
    }

    @Test
    public void errorMapperWithFunctions() {
        Result<Integer, DomainError> firstFailure = Result.failure(DomainError.businessRule("first"));
        Result<Integer, DomainError> secondFailure = Result.failure(DomainError.businessRule("second"));
        Result<Integer, DomainError> thirdFailure = Result.failure(DomainError.businessRule("third"));

        Result<Integer, DomainError> mappedResult = firstFailure
                .flatMap(r -> secondFailure)
                .flatMap(r -> thirdFailure);
        assertTrue(mappedResult.isFailure());
        assertEquals("first", mappedResult.getErrors().getFirst().message());
        assertEquals("second", mappedResult.getErrors().get(1).message());

        assertEquals(3, mappedResult.getErrors().size());
    }

    @Test
    public void dataPersistenceErrorMapping() {
        Result<Integer, DomainError> failureResult = Result.failure(DomainError.businessRule("first"));
        Result<Integer, DomainError> successResult = Result.success(42);
        Result<Integer, DomainError> secondFailureResult = Result.failure(DomainError.businessRule("second"));

        Result<Integer, DomainError> mappedFailure = failureResult
                .flatMap(r -> successResult)
                .flatMap(r -> secondFailureResult);
        assertTrue(mappedFailure.isFailure());
        assertEquals("first", mappedFailure.getErrors().getFirst().message());
        assertEquals(2, mappedFailure.getErrors().size());
    }

    @Test
    public void successMappingAndGetFinalElement() {
        Result<Integer, DomainError> firstSuccess = Result.success(10);
        Result<Integer, DomainError> secondSuccess = Result.success(20);
        Result<Integer, DomainError> thirdSuccess = Result.success(30);

        Result<Integer, DomainError> mappedResult = firstSuccess
                .flatMap(r1 -> secondSuccess)
                .flatMap(r2 -> thirdSuccess);
        assertTrue(mappedResult.isSuccess());
        assertEquals(30, mappedResult.getValue());
    }

    @Test
    public void successMappingAndGetFinalElementAndMap() {
        Result<Integer, DomainError> firstSuccess = Result.success(10);
        Result<Integer, DomainError> secondSuccess = Result.success(20);
        Result<Integer, DomainError> thirdSuccess = Result.success(30);

        Result<Integer, DomainError> mappedResult = firstSuccess
                .flatMap(r1 -> secondSuccess)
                .flatMap(r2 -> thirdSuccess)
                .map(value -> value + 5);
        assertTrue(mappedResult.isSuccess());
        assertEquals(35, mappedResult.getValue());
    }

    @Test
    public void successFlatMapChain() {
        Result<Integer, DomainError> result = Result.failure(DomainError.businessRule("first"));

        Result<Integer, DomainError> finalResult = result.flatMap(r -> Result.<Integer, DomainError>failure(DomainError.businessRule("second"))
                .flatMap(next -> Result.success(next)));
        assertTrue(finalResult.isFailure());
        assertEquals(2, finalResult.getErrors().size());
    }

    @Test
    public void successFlatMapChainAndMap() {

        Result<Integer, DomainError> finalResult = Result.<Integer, DomainError>success(2)
                .flatMap(r -> Result.<Integer, DomainError>success(5)
                        .flatMap(next -> Result.<Integer, DomainError>success(7)
                                .map(last -> r + next + last)));
        assertTrue(finalResult.isSuccess());
        assertEquals(14, finalResult.getValue());
    }

    @Test
    public void combineProceduresAndFunctions() {
        Result<Integer, DomainError> result = Result.failure(DomainError.businessRule("first"));

        Result<Integer, DomainError> finalResult = result
                .andThen(() -> Result.failure(DomainError.businessRule("second")))
                .flatMap(r -> Result.success(10));
        assertTrue(finalResult.isFailure());
        assertEquals(2, finalResult.getErrors().size());
    }

    @Test
    public void combineSuccessProceduresAndFunctions() {
        Result<Integer, DomainError> result = Result.success(3);

        Result<Integer, DomainError> finalResult = result
                .andThen(Result::success)
                .flatMap(r -> Result.success(10));
        assertTrue(finalResult.isSuccess());
        assertEquals(10, finalResult.getValue());
    }

    @Test
    public void combineSuccessFunctionsAndProceduresWithPDataPersistence() {
        Result<Integer, DomainError> result = Result.success(3);

        Result<Integer, DomainError> finalResult = result
                .flatMap(r -> Result.<Integer, DomainError>success(5)
                        .flatMap(s -> Result.<Integer, DomainError>success(6)
                                .flatMap(() -> Result.<Integer, DomainError>success(22)
                                        .map(() -> r + s))));
        assertTrue(finalResult.isSuccess());
        assertEquals(8, finalResult.getValue());
    }

    @Test
    public void mapOverFailureDoesNotChangeErrors() {
        Result<Integer, DomainError> failureResult = Result.failure(DomainError.businessRule("original error"));

        Result<Integer, DomainError> mappedResult = failureResult.map(value -> value + 10);

        assertTrue(mappedResult.isFailure());
        assertEquals(1, mappedResult.getErrors().size());
        assertEquals("original error", mappedResult.getErrors().getFirst().message());
    }

    @Test
    public void flatMapOverFailureDoesNotThrowsAnException() {
        Result<Integer, DomainError> failureResult = Result.failure(DomainError.businessRule("original error"));

        Result<Integer, DomainError> flatMappedResult = failureResult.safeFlatMap(value -> Result.success(value + 10));

        assertTrue(flatMappedResult.isFailure());
        assertEquals(1, flatMappedResult.getErrors().size());
        assertEquals("original error", flatMappedResult.getErrors().getFirst().message());
    }
}
