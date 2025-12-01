package toast.model.entities.users;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class FriendShipId implements Serializable {

    @Column(name = "user_one_id", nullable = false)
    private Long userOneId;

    @Column(name = "user_two_id", nullable = false)
    private Long userTwoId;

    public FriendShipId() {}

    public FriendShipId(Long userOneId, Long userTwoId) {
        this.userOneId = userOneId;
        this.userTwoId = userTwoId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendShipId that)) return false;
        return Objects.equals(userOneId, that.userOneId) && Objects.equals(userTwoId, that.userTwoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userOneId, userTwoId);
    }
}
