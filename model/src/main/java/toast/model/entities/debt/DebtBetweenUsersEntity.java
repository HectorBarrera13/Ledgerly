package toast.model.entities.debt;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebtBetweenUsersEntity extends DebtEntity {

    @Column(nullable = false)
    private UUID debtorId;

    @Column(nullable = false)
    private UUID creditorId;
}
