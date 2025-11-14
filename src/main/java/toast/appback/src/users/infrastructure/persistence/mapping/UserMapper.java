package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.*;
import toast.model.entities.users.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;
        return new User(
                UserId.load(userEntity.getUserId()),
                Name.create(userEntity.getFirstName(), userEntity.getLastName()).getValue(),
                Phone.create(userEntity.getPhone().getCountryCode(), userEntity.getPhone().getNumber()).getValue()
        );
    }
}
