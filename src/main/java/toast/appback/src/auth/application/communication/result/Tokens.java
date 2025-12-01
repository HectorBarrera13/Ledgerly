package toast.appback.src.auth.application.communication.result;

public record Tokens(
        Jwt accessToken,
        Jwt refreshToken
) {
}
