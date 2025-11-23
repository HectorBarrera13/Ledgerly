package toast.model.entities.users;

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
@Table(name = "user", indexes = {
        @Index(name = "idx_user_uuid", columnList = "userId", unique = true),
        @Index(name = "idx_user_created_at", columnList = "createdAt"),
        @Index(name = "idx_user_last_name", columnList = "lastName"),
        @Index(name = "idx_user_first_name", columnList = "firstName")
})
public class UserEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Embedded
    private PhoneEmbeddable phone;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}