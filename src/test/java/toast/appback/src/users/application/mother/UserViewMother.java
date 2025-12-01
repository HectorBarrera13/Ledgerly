package toast.appback.src.users.application.mother;

import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.User;

public class UserViewMother {
    public static UserView create(User user) {
        return new UserView(
                user.getUserId().getValue(),
                user.getName().getFirstName(),
                user.getName().getLastName(),
                user.getPhone().getNumber()
        );
    }
}
