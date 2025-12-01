package toast.model.entities.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toast.model.entities.CursorIdentifiable;

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
