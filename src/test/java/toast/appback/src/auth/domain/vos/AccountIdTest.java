package toast.appback.src.auth.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.domain.AccountId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountId Value Object Test")
class AccountIdTest {

    @Test
    @DisplayName("Should create AccountId from UUID")
    void testCreateAccountIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        AccountId accountId = AccountId.load(uuid);
        assertEquals(uuid, accountId.getValue());
    }

    @Test
    @DisplayName("Should generate a new AccountId")
    void testGenerateAccountId() {
        AccountId accountId1 = AccountId.generate();
        AccountId accountId2 = AccountId.generate();
        assertNotNull(accountId1);
        assertNotNull(accountId2);
        assertNotEquals(accountId1, accountId2);
    }

    @Test
    @DisplayName("Should correctly compare two AccountId instances")
    void testAccountIdEquality() {
        UUID uuid = UUID.randomUUID();
        AccountId accountId1 = AccountId.load(uuid);
        AccountId accountId2 = AccountId.load(uuid);
        assertEquals(accountId1, accountId2);
    }
}
