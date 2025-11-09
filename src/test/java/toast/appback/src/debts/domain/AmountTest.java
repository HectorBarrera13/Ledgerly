package toast.appback.src.debts.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AmountTest {

    @Test
    public void Amount_CreateValidAmount_AmountIsSuccessful() {
        String currency = "MXN";
        Double quantity = 100.00;
        Amount amount = new Amount(currency, quantity);
        assertNotNull(amount);
    }

    @Test
    public void Amount_InvalidCurrency_AmountIsFailed() {
        String currency = "MX";
        Double quantity = 100.00;
        var amount = Amount.create(currency, quantity);
        assertFalse(amount.isSuccess());
    }

    @Test
    public void Amount_InvalidQuantity_AmountIsFailed() {
        String currency = "MX";
        Double quantity = -10.00;
        var amount = Amount.create(currency, quantity);
        assertFalse(amount.isSuccess());
    }


}
