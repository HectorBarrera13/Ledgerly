package toast.appback.src.auth.domain;

import toast.appback.src.shared.domain.BusinessCode;

public enum AccountBusinessCode implements BusinessCode {
    SESSION_LIMIT_EXCEEDED;

    @Override
    public AccountBusinessCode code() {
        return this;
    }
}
