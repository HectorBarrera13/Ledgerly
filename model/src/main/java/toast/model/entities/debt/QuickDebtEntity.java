package toast.model.entities.debt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("QUICK_DEBT")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "quick_debt", indexes = {
        @Index(name = "idx_quick_debt_user_id", columnList = "userId"),
        @Index(name = "idx_quick_debt_target_user_name", columnList = "targetUserName")
})
public class QuickDebtEntity extends DebtEntity {

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String myRole;

    @Column(nullable = false)
    private String targetUserName;
}
