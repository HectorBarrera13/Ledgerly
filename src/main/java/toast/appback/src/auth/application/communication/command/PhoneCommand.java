package toast.appback.src.auth.application.communication.command;

public record PhoneCommand(
        String countryCode,
        String number
) {
}
