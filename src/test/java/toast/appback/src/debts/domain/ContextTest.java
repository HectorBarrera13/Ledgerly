package toast.appback.src.debts.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ContextTest {
    @Test
    public void Context_WithValidFortmat_ResultIsSuccesful() {
        var result = Context.create("Test", "Test");
        assertTrue(result.isSuccess());
    }

    @Test
    public void Context_WithPurposeEmptyDescriptionValid_ResultIsNotSuccesful() {
        var result = Context.create("", "Test");
        assertFalse(result.isSuccess());
    }

    @Test
    public void Context_WithPurposeTooLongDescriptionValid_ResultIsNotSuccesful() {
        var result = Context.create("Lorem ipsum dolor sit amet consectetur adipiscing elit quisque faucibus.", "Test1");
        assertFalse(result.isSuccess());
    }

    @Test
    public void Context_WithPurposeValidDescriptionEmpty_ResultIsSuccesful() {
        var result = Context.create("Test", "");
        assertTrue(result.isSuccess());
    }

    @Test
    public void Context_WithPurposeValidDescriptionTooLong_ResultIsNotSuccesful() {
        var result = Context.create("Test", "Lorem ipsum dolor sit amet consectetur adipiscing elit quisque faucibus ex sapien vitae pellentesque sem placerat in id cursus mi pretium tellus duis convallis tempus leo eu aenean sed diam urna tempor pulvinar vivamus fringilla." );
        assertFalse(result.isSuccess());
    }

}
