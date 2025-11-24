package toast.model.entities.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session", indexes = {
        @Index(name = "idx_session_session_id", columnList = "sessionId", unique = true),
        @Index(name = "idx_session_account_id", columnList = "account_id"),
        @Index(name = "idx_session_started_at", columnList = "startedAt"),
        @Index(name = "idx_session_expiration", columnList = "expiration")
})
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    private SessionStatusE sessionStatus;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    @Column(nullable = false, updatable = false)
    private Instant expiration;
}