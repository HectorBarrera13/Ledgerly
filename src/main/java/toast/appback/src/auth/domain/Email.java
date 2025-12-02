package toast.appback.src.auth.domain;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.Validators;
import toast.appback.src.shared.utils.result.Result;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final String FIELD_NAME = "email";
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

    public static Result<Email, DomainError> create(String email) {
        Result<EmailParts, DomainError> generalValidation = verifyGeneral(email);
        if (generalValidation.isFailure()) {
            return Result.failure(generalValidation.getErrors());
        }
        EmailParts parts = generalValidation.get();
        Result<Void, DomainError> localPartResult = verifyLocalPart(parts.local);
        if (localPartResult.isFailure()) {
            return Result.failure(localPartResult.getErrors());
        }
        Result<Void, DomainError> domainPartResult = verifyDomainPart(parts.domainTag);
        if (domainPartResult.isFailure()) {
            return Result.failure(domainPartResult.getErrors());
        }
        Result<Void, DomainError> tldPartResult = verifyTldPart(parts.tld);
        if (tldPartResult.isFailure()) {
            return Result.failure(tldPartResult.getErrors());
        }
        return Result.ok(new Email(parts.local, parts.domainTag, parts.tld));
    }

    public static Email load(String email) {
        return create(email)
                .orElseThrow(() -> new IllegalArgumentException("invalid email format: " + email));
    }

    private static Result<EmailParts, DomainError> verifyGeneral(String value) {
        if (value == null || value.isBlank()) {
            return Validators.emptyValue(FIELD_NAME);
        }

        if (value.length() > MAX_EMAIL_LENGTH) {
            return Validators.tooLong(FIELD_NAME, value, MAX_EMAIL_LENGTH);
        }

        if (value.startsWith(" ") || value.endsWith(" ")) {
            return Validators.invalidFormat(FIELD_NAME, value, "must not start or end with whitespace");
        }

        String[] split = value.split("@"); // Simple split to check basic structure
        if (split.length != 2) {
            return Validators.invalidFormat(FIELD_NAME, value, "must contain exactly one '@' character");
        }

        String domainPart = split[1];
        if (domainPart.length() > MAX_DOMAIN_PART_LENGTH) {
            return Validators
                    .invalidFormat(FIELD_NAME, value, "domain part cannot be longer than " + MAX_DOMAIN_PART_LENGTH + " characters");
        }
        String[] domainParts = domainPart.split("\\.", -1);
        if (domainParts.length != 2) {
            return Validators
                    .invalidFormat(FIELD_NAME, value, "domain part must contain exactly one dot (.) separating domain and TLD");
        }
        String local = split[0];
        String domain = domainParts[0].toLowerCase();
        String tldPart = domainParts[1].toLowerCase();
        return Result.ok(new EmailParts(local, domain, tldPart));
    }

    private static Result<Void, DomainError> verifyLocalPart(String local) {
        if (local.isBlank()) {
            return Validators.emptyValue(FIELD_NAME);
        }

        if (local.startsWith(".")) {
            return Validators.mustNotStartWith(FIELD_NAME, local, ".");
        }

        if (local.endsWith(".")) {
            return Validators
                    .invalidFormat(FIELD_NAME, local, "local part must not end with a dot");
        }

        if (local.contains("..")) {
            return Validators
                    .invalidFormat(FIELD_NAME, local, "local part must not contain consecutive dots");
        }

        if (local.length() > MAX_LOCAL_PART_LENGTH) {
            return Validators
                    .invalidFormat(FIELD_NAME, local, "local part cannot be longer than " + MAX_LOCAL_PART_LENGTH + " characters");
        }

        if (!PERMITTED_LOCAL_CHARACTERS_PATTERN.matcher(local).matches()) {
            return Validators.invalidFormat(FIELD_NAME, local, "local part contains invalid characters");
        }
        return Result.ok();
    }

    private static Result<Void, DomainError> verifyDomainPart(String domain) {
        if (domain.isBlank()) {
            return Validators.emptyValue(FIELD_NAME);
        }

        if (domain.startsWith("-") || domain.startsWith(".")) {
            return Validators
                    .invalidFormat(FIELD_NAME, domain, "domain part must not start with a hyphen or dot");
        }

        if (domain.endsWith("-") || domain.endsWith(".")) {
            return Validators
                    .invalidFormat(FIELD_NAME, domain, "domain part must not end with a hyphen or dot");
        }

        if (domain.contains("..")) {
            return Validators
                    .invalidFormat(FIELD_NAME, domain, "domain part must not contain consecutive dots");
        }

        if (domain.contains(" ")) {
            return Validators
                    .invalidFormat(FIELD_NAME, domain, "domain part must not contain spaces");
        }

        if (!PERMITTED_DOMAIN_CHARACTERS_PATTERN.matcher(domain).matches()) {
            return Validators
                    .invalidFormat(FIELD_NAME, domain, "domain part contains invalid characters");
        }
        return Result.ok();
    }

    private static Result<Void, DomainError> verifyTldPart(String tld) {
        if (tld.isBlank()) {
            return Validators.emptyValue(FIELD_NAME);
        }
        if (tld.contains("..")) {
            return Validators
                    .invalidFormat(FIELD_NAME, tld, "TLD part must not contain consecutive dots");
        }
        if (tld.length() < 2 || tld.length() > 24) {
            return Validators
                    .invalidFormat(FIELD_NAME, tld, "TLD part must be between 2 and 24 characters long");
        }
        return Result.ok();
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

    private record EmailParts(String local, String domainTag, String tld) {
    }
}
