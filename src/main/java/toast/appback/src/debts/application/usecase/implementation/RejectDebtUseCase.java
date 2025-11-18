package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.RejectDebtCommand;
import toast.appback.src.debts.application.communication.command.ReportDebtPaymentCommand;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.contract.RejectDebt;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

public class RejectDebtUseCase implements RejectDebt {
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public RejectDebtUseCase(DebtRepository debtRepository, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Debt execute(RejectDebtCommand command) {
        Debt debt = debtRepository.findById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        User actor = userRepository.findById(command.actorId())
                .orElseThrow(() -> new UserNotFound(command.actorId()));

        validateAuthorization(debt, actor.getUserId());

        Result<Void, DomainError> result = debt.reject();

        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        debtRepository.save(debt);

        return debt;
    }

    private void validateAuthorization(Debt debt, UserId actorId) {

        if (debt instanceof DebtBetweenUsers) {
            DebtBetweenUsers specificDebt = (DebtBetweenUsers) debt;
            boolean isDebtor = specificDebt.getDebtorId().equals(actorId);
            boolean isCreditor = specificDebt.getCreditorId().equals(actorId);
            if (!isDebtor && !isCreditor) {
                throw new UnauthorizedActionException("El actor debe estar involucrado en la deuda");
            }
        } else if (debt instanceof QuickDebt) {
            QuickDebt quickDebt = (QuickDebt) debt;
            if (!quickDebt.getUserId().equals(actorId)) {
                throw new UnauthorizedActionException("Solo el creador puede marcar esta como pagada.");
            }
        }
    }
}
