package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.RejectDebtPaymentCommand;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.contract.RejectDebtPayment;
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

public class RejectDebtPaymentUseCase implements RejectDebtPayment {
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public RejectDebtPaymentUseCase(DebtRepository debtRepository, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Debt execute(RejectDebtPaymentCommand command) {
        Debt debt = debtRepository.findById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        User actor = userRepository.findById(command.actorId())
                .orElseThrow(() -> new UserNotFound(command.actorId()));

        validateAuthorization(debt, actor.getUserId());

        Result<Void, DomainError> result = debt.rejectPayment();

        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        debtRepository.save(debt);

        return debt;
    }

    private void validateAuthorization(Debt debt, UserId actorId) {

        if (debt instanceof DebtBetweenUsers) {
            DebtBetweenUsers specificDebt = (DebtBetweenUsers) debt;
            if (!specificDebt.getCreditorId().equals(actorId)) {
                throw new UnauthorizedActionException("Solo el creditor puede marcar esta deuda como pagada.");
            }
        } else if (debt instanceof QuickDebt) {
            QuickDebt quickDebt = (QuickDebt) debt;
            if (!quickDebt.getUserId().equals(actorId)) {
                throw new UnauthorizedActionException("Solo el creador puede marcar esta como pagada.");
            }
        }
    }
}
