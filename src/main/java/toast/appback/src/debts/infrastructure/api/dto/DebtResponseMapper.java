package toast.appback.src.debts.infrastructure.api.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.result.*;
import toast.appback.src.debts.infrastructure.api.dto.response.*;
import toast.appback.src.users.infrastructure.service.UserProfilePictureService;

@Service
@RequiredArgsConstructor
public class DebtResponseMapper {
    private final UserProfilePictureService userProfilePictureService;

    public DebtResponseInt toDebtResponse(DebtView debtView) {
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

    public DebtResponse toDebtResponseBasic(DebtBaseView debtView) {
        return new DebtResponse(
                debtView.debtId(),
                debtView.purpose(),
                debtView.description(),
                debtView.amount(),
                debtView.currency(),
                debtView.status()
        );
    }

    public QuickDebtResponse toQuickDebtResponse(QuickDebtView quickDebt) {
        return new QuickDebtResponse(
                quickDebt.debtId(),
                quickDebt.purpose(),
                quickDebt.description(),
                quickDebt.amount(),
                quickDebt.currency(),
                quickDebt.status(),
                toUserSummaryResponse(quickDebt.userSummary()),
                quickDebt.role(),
                quickDebt.targetUserName()
        );
    }

    public DebtBetweenUsersResponse toDebtBetweenUsersResponse(DebtBetweenUsersView debtBetweenUsers) {
        return new DebtBetweenUsersResponse(
                debtBetweenUsers.debtId(),
                debtBetweenUsers.purpose(),
                debtBetweenUsers.description(),
                debtBetweenUsers.amount(),
                debtBetweenUsers.currency(),
                debtBetweenUsers.status(),
                toUserSummaryResponse(debtBetweenUsers.debtorSummary()),
                toUserSummaryResponse(debtBetweenUsers.creditorSummary())
        );
    }

    public UserSummaryResponse toUserSummaryResponse(UserSummaryView userSummaryView) {
        return new UserSummaryResponse(
                userSummaryView.userId(),
                userSummaryView.userFirstName(),
                userSummaryView.userLastName(),
                userProfilePictureService.getProfileUri(
                        userSummaryView.userId()
                )
        );
    }

    public DebtBetweenUsersResponse toGroupDebtResponse(DebtBetweenUsersView debt) {
        return new DebtBetweenUsersResponse(
                debt.debtId(),
                debt.purpose(),
                debt.description(),
                debt.amount(),
                debt.currency(),
                debt.status()
                , toUserSummaryResponse(debt.debtorSummary())
                , toUserSummaryResponse(debt.creditorSummary())
        );
    }
}
