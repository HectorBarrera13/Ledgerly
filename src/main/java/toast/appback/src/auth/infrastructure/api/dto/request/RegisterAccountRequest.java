package toast.appback.src.auth.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;

public record RegisterAccountRequest(
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email,
        String password,
        PhoneDto phone
) {
    public RegisterAccountCommand toCommand() {
        return new RegisterAccountCommand(
                firstName,
                lastName,
                email,
                password,
                new RegisterAccountCommand.Phone(
                    phone.countryCode(),
                    phone.number()
                )
        );
    }

    public record PhoneDto(
            @JsonProperty("country_code")
            String countryCode,
            String number
    ) {}
}
