package toast.appback.src.debts.infrastructure.api.dto;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;

public class DebtResponseMapper {

    public static DebtResponse toDebtResponse( DebtView debtView ) {
        return new DebtResponse(
                debtView.debtId(),
                debtView.purpose(),
                debtView.description(),
                debtView.currency(),
                debtView.amount(),
                debtView.debtorName(),
                debtView.creditorName(),
                debtView.status()
        );
    }
}
