package toast.appback.src.debts.infrastructure.api.dto;

import toast.appback.src.debts.application.communication.result.*;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.infrastructure.api.dto.response.*;
import toast.appback.src.groups.application.communication.result.GroupDebtView;

public class DebtResponseMapper {

    public static DebtResponseInt toDebtResponse(DebtView debtView) {
        if (debtView instanceof QuickDebtView quickDebt) {
            return toQuickDebtResponse(quickDebt);
        }
        if (debtView instanceof DebtBetweenUsersView betweenUsersDebt) {
            return toDebtBetweenUsersResponse(betweenUsersDebt);
        }

        throw new IllegalArgumentException(
                "Tipo de deuda no soportado: " + debtView.getClass().getSimpleName()
        );
    }

    public static DebtResponse toDebtResponseBasic(DebtBaseView debtView) {
        return new DebtResponse(
                debtView.debtId(),
                debtView.purpose(),
                debtView.description(),
                debtView.amount(),
                debtView.currency(),
                debtView.status()
        );
    }

    public static QuickDebtResponse toQuickDebtResponse(QuickDebtView quickDebt) {
        return new QuickDebtResponse(
                quickDebt.debtId(),
                quickDebt.purpose(),
                quickDebt.description(),
                quickDebt.amount(),
                quickDebt.currency(),
                quickDebt.status(),
                DebtResponseMapper.toUserSummaryResponse(quickDebt.userSummary()),
                quickDebt.role(),
                quickDebt.targetUserName()
        );
    }

    public static DebtBetweenUsersResponse toDebtBetweenUsersResponse(DebtBetweenUsersView debtBetweenUsers) {
        return new DebtBetweenUsersResponse(
                debtBetweenUsers.debtId(),
                debtBetweenUsers.purpose(),
                debtBetweenUsers.description(),
                debtBetweenUsers.amount(),
                debtBetweenUsers.currency(),
                debtBetweenUsers.status(),
                DebtResponseMapper.toUserSummaryResponse(debtBetweenUsers.debtorSummary()),
                DebtResponseMapper.toUserSummaryResponse(debtBetweenUsers.creditorSummary())
        );
    }

    public static UserSummaryResponse toUserSummaryResponse(UserSummaryView userSummaryView) {
        return new UserSummaryResponse(
                userSummaryView.userId(),
                userSummaryView.userFirstName(),
                userSummaryView.userLastName()
        );
    }

    public static DebtResponse toGroupDebtResponse(GroupDebtView groupDebtView) {
        return new DebtResponse(
                groupDebtView.DebtId(),
                groupDebtView.purpose(),
                groupDebtView.description(),
                groupDebtView.amount().longValue(),
                groupDebtView.currency(),
                groupDebtView.status()
        );
    }
}
