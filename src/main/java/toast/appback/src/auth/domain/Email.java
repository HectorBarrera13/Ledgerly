package toast.appback.src.auth.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.domain.DomainError;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final int MAX_EMAIL_LENGTH = 320;
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final Pattern PERMITTED_LOCAL_CHARACTERS_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+$");
    private static final int MAX_DOMAIN_PART_LENGTH = 255;
    private static final Pattern PERMITTED_DOMAIN_CHARACTERS_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");

    private final String local;
    private final String domain;
    private final String tld;

    private Email(String local, String domain, String tld) {
        this.local = local;
        this.domain = domain;
        this.tld = tld;
    }

    public String getLocal() {
        return local;
    }

    public String getDomain() {
        return domain;
    }

    public String getTld() {
        return tld;
    }

    public String getValue() {
        return local + "@" + domain + "." + tld;
    }

    public static Result<Email, DomainError> create(String email) {
        System.out.println("Creating email: " + email);
        Result<EmailParts, DomainError> generalValidation = verifyGeneral(email);
        if (generalValidation.isFailure()) {
            return Result.failure(generalValidation.getErrors());
        }
        EmailParts parts = generalValidation.getValue();
        return verifyLocalPart(parts.local)
                .captureFirstError(() -> verifyDomainPart(parts.domainTag))
                .captureFirstError(() -> verifyTldPart(parts.tld))
                .map(() -> new Email(parts.local, parts.domainTag, parts.tld));
    }

    public static Email load(String email) {
        String[] split = email.split("@");
        String local = split[0];
        String domainPart = split[1];
        String[] domainSplit = domainPart.split("\\.", -1);
        String domain = domainSplit[0];
        String tld = domainSplit[1];
        return new Email(local, domain, tld);
    }

    private static Result<EmailParts, DomainError> verifyGeneral(String value) {
        if (value == null || value.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }

        if (value.length() > MAX_EMAIL_LENGTH) {
            return Validators.TOO_LONG("email", value, MAX_EMAIL_LENGTH);
        }

        if (value.startsWith(" ") || value.endsWith(" ")) {
            return Result.failure(
                    DomainError.validation("email", "email must not start or end with whitespace")
                            .withDetails("value: '" + value + "'")
            );
        }

        String[] split = value.split("@"); // Simple split to check basic structure
        if (split.length != 2) {
            return Validators.INVALID_FORMAT("email", value, "must contain exactly one '@' character");
        }

        String domainPart = split[1];
        if (domainPart.length() > MAX_DOMAIN_PART_LENGTH) {
            return Result.failure(
                    DomainError.validation("email", "domain part cannot be longer than " + MAX_DOMAIN_PART_LENGTH + " characters")
                            .withDetails("value: '" + domainPart + "'"));
        }
        String[] domainParts = domainPart.split("\\.", -1);
        if (domainParts.length != 2) {
            return Validators.INVALID_FORMAT("email", value, "domain part must contain exactly one dot (.) separating domain and TLD");
        }
        String local = split[0];
        String domain = domainParts[0].toLowerCase();
        String tldPart = domainParts[1].toLowerCase();
        return Result.success(new EmailParts(local, domain, tldPart));
    }

    private record EmailParts(String local, String domainTag, String tld) {}

    private static Result<Void, DomainError> verifyLocalPart(String local) {
        if (local.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }

        if (local.startsWith(".")) {
            return Validators.MUST_NOT_START_WITH("email", local, ".");
        }

        if (local.endsWith(".")) {
            return Result.failure(DomainError.validation("email", "local part must not end with a dot")
                    .withDetails("value: '" + local + "'"));
        }

        if (local.contains("..")) {
            return Result.failure(DomainError.validation("email", "local part must not contain consecutive dots")
                    .withDetails("value: '" + local + "'"));
        }

        if (local.length() > MAX_LOCAL_PART_LENGTH) {
            return Result.failure(
                    DomainError.validation("email", "local part cannot be longer than " + MAX_LOCAL_PART_LENGTH + " characters")
                            .withDetails("value: '" + local + "'"));
        }

        if (!PERMITTED_LOCAL_CHARACTERS_PATTERN.matcher(local).matches()) {
            return Result.failure(
                    DomainError.validation("email", "local part contains invalid characters")
                            .withDetails("value: '" + local + "'")
            );
        }
        return Result.success();
    }

    private static Result<Void, DomainError> verifyDomainPart(String domain) {
        if (domain.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }

        if (domain.startsWith("-") || domain.startsWith(".")) {
            return Result.failure(DomainError.validation("email", "domain part must not start with a hyphen or dot")
                    .withDetails("value: '" + domain + "'"));
        }

        if (domain.endsWith("-") ||  domain.endsWith(".")) {
            return Result.failure(DomainError.validation("email", "domain part must not end with a hyphen or dot")
                    .withDetails("value: '" + domain + "'"));
        }

        if (domain.contains("..")) {
            return Result.failure(DomainError.validation("email", "domain part must not contain consecutive dots")
                    .withDetails("value: '" + domain + "'"));
        }

        if (domain.contains(" ")) {
            return Result.failure(DomainError.validation("email", "domain part must not contain spaces")
                    .withDetails("value: '" + domain + "'"));
        }

        if (!PERMITTED_DOMAIN_CHARACTERS_PATTERN.matcher(domain).matches()) {
            return Result.failure(
                    DomainError.validation("email", "domain part contains invalid characters")
                            .withDetails("value: '" + domain + "'")
            );
        }
        return Result.success();
    }

    private static Result<Void, DomainError> verifyTldPart(String tld) {
        if (tld.isBlank()) {
            return Validators.EMPTY_VALUE("email");
        }
        if (tld.contains("..")) {
            return Result.failure(DomainError.validation("email", "TLD part must not contain consecutive dots")
                    .withDetails("value: '" + tld + "'"));
        }
        if (tld.length() < 2 || tld.length() > 24) {
            return Result.failure(
                    DomainError.validation("email", "TLD part must be between 2 and 24 characters long")
                            .withDetails("Value: '" + tld + "'"));
        }
        return Result.success();
    }

    @Override
    public String toString() {
        return "Email{" +
                "local='" + local + '\'' +
                ", domain='" + domain + '\'' +
                ", tld='" + tld + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email email)) return false;
        return Objects.equals(local, email.local) && Objects.equals(domain, email.domain) && Objects.equals(tld, email.tld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(local, domain, tld);
    }
}
