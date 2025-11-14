package toast.model.entities.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Entity
@Data
@Table(name = "friend_ship")
public class FriendShipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_user_id")
    private UserEntity firstUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_user_id")
    private UserEntity secondUser;

    private Instant createdAt;

    @Override
    public String toString() {
        return "FriendShipEntity{" +
                "id=" + id +
                ", firstUser=" + (firstUser != null ? firstUser.getUserId() : null) +
                ", secondUser=" + (secondUser != null ? secondUser.getUserId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
