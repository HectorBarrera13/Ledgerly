package toast.model.entities.debt;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "debt_between_users", indexes = {
        @Index(name = "idx_between_debt_debt_id", columnList = "debt_id", unique = true),
        @Index(name = "idx_between_debt_created_at", columnList = "created_at")
})
@DiscriminatorValue("DEBT_BETWEEN_USERS")
@PrimaryKeyJoinColumn(name = "id")
public class DebtBetweenUsersEntity extends DebtEntity {

    @Column(nullable = false)
    private UUID debtorId;

    @Column(nullable = false)
    private UUID creditorId;
}
