package toast.appback.src.auth.domain;

import toast.appback.src.shared.domain.BusinessCode;

/**
 * Códigos de negocio específicos para el agregado {@link Account} (errores de sesión, límites, etc.).
 */
public enum AccountBusinessCode implements BusinessCode {
    SESSION_LIMIT_EXCEEDED,
    SESSION_NOT_FOUND,
    SESSION_ALREADY_REVOKED,
    SESSION_REVOKED;

    @Override
    public AccountBusinessCode code() {
        return this;
    }
}
