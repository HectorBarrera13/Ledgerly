package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.BusinessCode;

public enum DebtBusinessCode implements BusinessCode {
    STATUS_NOT_PENDING,
    DEBT_NO_ACCEPTED;

    @Override
    public DebtBusinessCode code() {
        return this;
    }
}
