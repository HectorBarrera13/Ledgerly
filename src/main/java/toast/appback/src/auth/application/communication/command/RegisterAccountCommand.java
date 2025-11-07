package toast.appback.src.auth.application.communication.command;

public record RegisterAccountCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        Phone phone
) {
    public record Phone(
            String countryCode,
            String number
    ) {
    }
}
