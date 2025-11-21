package toast.model.entities.debt;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "debt_between_users")
public class DebtBetweenUsersEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID debtId;

    @Column(nullable = false)
    private UUID debtorId;

    @Column(nullable = false)
    private UUID creditorId;
}
