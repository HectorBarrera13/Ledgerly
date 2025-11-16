package toast.appback.src.users.application.mother;

import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.DefaultUser;
import toast.appback.src.users.domain.User;

public class UserMother {

    private static final DefaultUser defaultUser = new DefaultUser();

    public static User validUser() {
        return defaultUser.create(
                new CreateUserCommand(
                        "John",
                        "Doe",
                        "+23",
                        "1234567890"
                )
        ).orElseThrow(result -> new IllegalStateException("UserMother failed to create valid user\n"+result));
    }
}
