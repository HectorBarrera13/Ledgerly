package toast.model.entities.debt;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quick_debt", indexes =  {
        @Index(name = "idx_quick_debt_uuid", columnList = "debtId", unique = true),
        @Index(name = "idx_quick_debt_created_at", columnList = "createdAt")
})
@DiscriminatorValue("QUICK_DEBT")
@PrimaryKeyJoinColumn(name = "id")
public class QuickDebtEntity extends DebtEntity {

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String myRole;

    @Column(nullable = false)
    private String targetUserName;
}
