package toast.appback.src.quickDebt.domain;

import toast.appback.src.shared.domain.BusinessCode;

public enum QuickDebtBusinessCode implements BusinessCode {
    DEBT_NOT_PENDING;

    @Override
    public BusinessCode code() {
        return this;
    }
}
