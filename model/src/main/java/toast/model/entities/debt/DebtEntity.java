package toast.model.entities.debt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "debt", indexes = {
        @Index(name = "idx_debt_created_at", columnList = "createdAt"),
        @Index(name = "idx_debt_purpose", columnList = "purpose"),
        @Index(name = "idx_debt_currency", columnList = "currency"),
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "debt_type", discriminatorType = DiscriminatorType.STRING)
public class DebtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "debt_id", nullable = false, unique = true, updatable = false)
    private UUID debtId;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt;
}
