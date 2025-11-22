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
public class QuickDebtEntity extends DebtEntity{

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private String userName;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String targetUserName;

    @Column(nullable = false, updatable = false)
    private java.time.Instant createdAt;
}
