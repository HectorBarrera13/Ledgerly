package toast.model.entities.debt;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quick_debt", indexes =  {
        @Index(name = "idx_quick_debt_uuid", columnList = "uuid", unique = true),
        @Index(name = "idx_quick_debt_created_at", columnList = "createdAt")
})
public class QuickDebtEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String targetUserName;

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt;
}
