package toast.model.entities.users;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friend_ship")
public class FriendShipEntity {

    @EmbeddedId
    private FriendShipId id;

    private Instant createdAt;
}
