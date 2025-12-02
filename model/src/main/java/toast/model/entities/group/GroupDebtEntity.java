package toast.model.entities.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toast.model.entities.debt.DebtBetweenUsersEntity;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "group_debts"
)
public class GroupDebtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="debt_id", referencedColumnName = "debt_id")
    private DebtBetweenUsersEntity debt;

    @ManyToOne(optional = false)
    @JoinColumn(name="group_id")
    private GroupEntity group;

    @Column(name ="added_at",  nullable = false)
    private Instant addedAt;
}
