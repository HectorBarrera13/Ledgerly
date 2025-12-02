package toast.appback.src.groups.domain.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class GroupIdTest {
    @Test
    void testGenerateReturnsNonNullUUID() {
        GroupId groupId = GroupId.generate();
        assertNotNull(groupId.getValue());
    }

    @Test
    void testLoadUsesProvidedUUID() {
        UUID uuid = UUID.randomUUID();
        GroupId groupId = GroupId.load(uuid);

        assertEquals(uuid, groupId.getValue());
    }

    @Test
    void testGenerateProducesDifferentUUIDs() {
        GroupId id1 = GroupId.generate();
        GroupId id2 = GroupId.generate();

        assertNotEquals(id1.getValue(), id2.getValue());
    }

}
