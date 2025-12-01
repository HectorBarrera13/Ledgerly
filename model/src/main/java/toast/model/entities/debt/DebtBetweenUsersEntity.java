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
@Table(name = "debt_between_users", indexes = {
        @Index(name = "idx_debt_between_users_debtor_id", columnList = "debtorId"),
        @Index(name = "idx_debt_between_users_creditor_id", columnList = "creditorId")
})
@DiscriminatorValue("DEBT_BETWEEN_USERS")
@PrimaryKeyJoinColumn(name = "id")
public class DebtBetweenUsersEntity extends DebtEntity {

    @Column(nullable = false)
    private UUID debtorId;

    @Column(nullable = false)
    private UUID creditorId;
}
