package toast.model.entities.debt;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "debt", indexes =  {
        @Index(name = "idx_debt_uuid", columnList = "uuid", unique = true),
        @Index(name = "idx_debt_created_at", columnList = "createdAt")
})
public class DebtEntity {

    @Id()
    @Column(nullable = false, unique = true, updatable = false)
    private UUID debtId;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String debtorName;

    @Column(nullable = false)
    private String creditorName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt;
}
