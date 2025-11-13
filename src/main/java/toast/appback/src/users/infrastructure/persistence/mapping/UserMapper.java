package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.users.domain.*;
import toast.model.entities.users.PhoneEmbeddable;
import toast.model.entities.users.UserEntity;

public class UserMapper {
    public static UserEntity toEntity(User user, UserEntity existingEntity) {
        UserEntity userEntity = existingEntity != null ? existingEntity : new UserEntity();
        userEntity.setUserId(user.getUserId().getValue());
        userEntity.setFirstName(user.getName().getFirstName());
        userEntity.setLastName(user.getName().getLastName());

        PhoneEmbeddable phone = new PhoneEmbeddable();
        phone.setCountryCode(user.getPhone().getCountryCode());
        phone.setNumber(user.getPhone().getNumber());
        userEntity.setPhone(phone);

        return userEntity;
    }

    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;
        return new User(
                UserId.load(userEntity.getUserId()),
                Name.create(userEntity.getFirstName(), userEntity.getLastName()).getValue(),
                Phone.create(userEntity.getPhone().getCountryCode(), userEntity.getPhone().getNumber()).getValue()
        );
    }
}
