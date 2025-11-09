package toast.appback.src.debts.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatusTest {

    @Test
    void testCreatedStatusValue() {
        assertEquals("Creada", Status.CREATED.value(), "El valor de CREATED debe ser 'Creada'");
    }

    @Test
    void testSentStatusValue() {
        assertEquals("Enviada", Status.SENT.value(), "El valor de SENT debe ser 'Enviada'");
    }

    // Repetir para ACCEPTED, REJECTED, y PAID...
    @Test
    void testAcceptedStatusValue() {
        assertEquals("Aceptada", Status.ACCEPTED.value(), "El valor de ACCEPTED debe ser 'Aceptada'");
    }

    @Test
    void testRejectedStatusValue() {
        assertEquals("Rechazada", Status.REJECTED.value(), "El valor de REJECTED debe ser 'Rechazada'");
    }

    @Test
    void testPaidStatusValue() {
        assertEquals("Pagada", Status.PAID.value(), "El valor de PAID debe ser 'Pagada'");
    }
}