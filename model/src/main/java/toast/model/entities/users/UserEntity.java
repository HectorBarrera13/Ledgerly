package toast.model.entities.users;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "user", indexes = {
        @Index(name = "idx_user_uuid", columnList = "uuid", unique = true)
})
public class UserEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID userId;

    @OneToMany(mappedBy = "firstUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendShipEntity> friends = new ArrayList<>();

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Embedded
    private PhoneEmbeddable phone;

    public String getPhoneAsString() {
        if (phone == null) {
            return null;
        }
        return phone.getCountryCode() + "-" + phone.getNumber();
    }
}