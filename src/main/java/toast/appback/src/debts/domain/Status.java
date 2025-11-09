package toast.appback.src.debts.domain;

import toast.appback.src.shared.Validators;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.shared.types.Result;

import java.util.Map;

public record Status(String value) {
    private static final String FIELD_STATUS = "status";
    public static final Status CREATED    = new Status("Creada");
    public static final Status SENT   = new Status("Enviada");
    public static final Status ACCEPTED  = new Status("Aceptada");
    public static final Status REJECTED = new Status("Rechazada");
    public static final Status PAID = new Status("Pagada");

    private static final Map<String, Status> CANON = Map.of(
            CREATED.value, CREATED,
            SENT.value, SENT,
            ACCEPTED.value, ACCEPTED,
            REJECTED.value, REJECTED,
            PAID.value, PAID
    );

    public String getStatus() {
        return value;
    }
}
