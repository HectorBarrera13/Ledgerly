package toast.appback.src.users.infrastructure.persistence.mapping;

import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.*;
import toast.model.entities.users.PhoneEmbeddable;
import toast.model.entities.users.UserEntity;

import java.util.List;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(user.getId().value());
        userEntity.setFirstName(user.getName().firstName());
        userEntity.setLastName(user.getName().lastName());

        PhoneEmbeddable phone = new PhoneEmbeddable();
        phone.setCountryCode(user.getPhone().countryCode());
        phone.setNumber(user.getPhone().number());
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
