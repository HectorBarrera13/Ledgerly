package toast.model.entities.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toast.model.entities.users.UserEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_account_uuid", columnList = "accountId", unique = true),
        @Index(name = "idx_account_created_at", columnList = "createdAt"),
        @Index(name = "idx_account_last_updated_at", columnList = "lastUpdatedAt"),
        @Index(name = "idx_account_last_login", columnList = "lastLogin")
})
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID accountId;

    @OneToOne(cascade = CascadeType.MERGE)
    private UserEntity user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionEntity> sessions = new ArrayList<>();

    @Column(nullable = false)
    private Instant lastUpdatedAt;
}
