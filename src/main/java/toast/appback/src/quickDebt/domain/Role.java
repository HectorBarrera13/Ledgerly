package toast.appback.src.quickDebt.domain;

import java.util.Map;

public record Role(String value) {
    private static final String FIELD_ROLE = "role";
    public static final Role DEBTOR = new Role("Debtor");
    public static final Role CREDITOR = new Role("Creditor");

    private static final Map<String, Role> CANON = Map.of(
            DEBTOR.value, DEBTOR,
            CREDITOR.value, CREDITOR
    );

    public String getRole() {
        return value;
    }

}
