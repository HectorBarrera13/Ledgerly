package toast.appback.src.users.application.communication.command;

public record ChangeUserNameCommand(
        String firstName,
        String lastName
) {
}
