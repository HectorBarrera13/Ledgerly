package toast.appback.src.auth.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.Validators;
import toast.appback.src.shared.errors.DomainError;
import java.util.Set;
import java.util.regex.Pattern;

public record Email(String local, String domain) {
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9.%+-]");

    // TODO: Maybe changes to a more flexible domain validation in the future
    private static final Set<String> validDomains = Set.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com"
    );

    public static Result<Email, DomainError> create(String email) {
        Result<EmailParts, DomainError> generalValidation = verifyGeneral(email);
        if (generalValidation.isFailure()) {
            return Result.failure(generalValidation.getErrors());
        }
        EmailParts parts = generalValidation.getValue();
        return verifyLocalPart(parts.local)
                .flatMap(r -> verifyDomain(parts.domain)
                        .map(w -> new Email(parts.local(), parts.domain())));
    }

    private static Result<EmailParts, DomainError> verifyGeneral(String value) {
        if (value == null || value.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }

        String[] split = value.split("@"); // Simple split to check basic structure
        if (split.length != 2) {
            return Validators.INVALID_FORMAT("email", value, "Must contain exactly one '@' character");
        }
        return Result.success(new EmailParts(split[0], split[1]));
    }

    private record EmailParts(String local, String domain) {}

    private static Result<EmailParts, DomainError> verifyDomain(String domain) {
        if (!validDomains.contains(domain)) {
            return Validators.INVALID_FORMAT("domain email", domain, "Is not a valid email domain");
        }
        return Result.success();
    }

    private static Result<Void, DomainError> verifyLocalPart(String local) {
        if (local.startsWith(".")) {
            return Validators.MUST_NOT_START_WITH("user name", local, ".");
        }

        if (SPECIAL_CHARACTERS_PATTERN.matcher(local).matches()) {
            return Validators.INVALID_FORMAT("email", local, "Contains invalid characters");
        }

        if (local.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }

        if (local.length() > 64) {
            return Validators.TOO_LONG("user name", local, 64);
        }
        return Result.success();
    }

    public String getValue() {
        return local + "@" + domain;
    }

    public String getLocal() {
        return local;
    }

    public String getDomain() {
        return domain;
    }
}
