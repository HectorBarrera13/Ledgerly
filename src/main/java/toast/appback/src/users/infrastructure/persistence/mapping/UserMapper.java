package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.*;
import toast.model.entities.users.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;
        return User.load(
                UserId.load(userEntity.getUserId()),
                Name.load(userEntity.getFirstName(), userEntity.getLastName()),
                Phone.load(userEntity.getPhone().getCountryCode(), userEntity.getPhone().getNumber()),
                userEntity.getCreatedAt()
        );
    }
}
