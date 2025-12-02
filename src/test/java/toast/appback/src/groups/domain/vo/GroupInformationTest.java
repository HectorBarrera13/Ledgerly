package toast.appback.src.groups.domain.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GroupInformationTest {

    @Test
    void testCreateValidInformation() {
        var result = GroupInformation.create("Grupo A", "Descripción correcta");

        assertTrue(result.isOk());
        assertEquals("Grupo A", result.get().getName());
        assertEquals("Descripción correcta", result.get().getDescription());
    }

    @Test
    void testNameEmptyFails() {
        var result = GroupInformation.create("", "Descripcion");

        assertTrue(result.isFailure());
    }

    @Test
    void testDescriptionEmptyFails() {
        var result = GroupInformation.create("Grupo", "");

        assertTrue(result.isFailure());
    }

    @Test
    void testNameTooLongFails() {
        String longName = "a".repeat(31);

        var result = GroupInformation.create(longName, "Ok");

        assertTrue(result.isFailure());
    }

    @Test
    void testDescriptionTooLongFails() {
        String longDescription = "a".repeat(301);

        var result = GroupInformation.create("Grupo", longDescription);

        assertTrue(result.isFailure());
    }

    @Test
    void testLoadThrowsOnInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            GroupInformation.load("", "Descripcion");
        });
    }

    @Test
    void testLoadValid() {
        var info = GroupInformation.load("Grupo", "Desc");

        assertEquals("Grupo", info.getName());
        assertEquals("Desc", info.getDescription());
    }
}
