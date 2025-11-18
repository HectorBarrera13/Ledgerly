package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.usecase.contract.CreateQuickDebt;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateQuickDebtUseCase implements CreateQuickDebt {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;

    public CreateQuickDebtUseCase(UserRepository userRepository, DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }

    @Override
    public QuickDebt execute(CreateQuickDebtCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFound(command.userId()));

        DebtId debtid = DebtId.generate();
        UserId userId = user.getUserId();
        DebtMoney debtMoney = DebtMoney.create(command.currency(), command.amount()).orElseThrow(CreationDebtException::new);
        Context context = Context.create(command.purpose(), command.description()).orElseThrow(CreationDebtException::new);
        Role role = Role.create(command.role()).orElseThrow(CreationDebtException::new);
        TargetUser targetUser = TargetUser.create(command.targetUser()).orElseThrow(CreationDebtException::new);

        QuickDebt newQuickDebt = new QuickDebt(
                debtid,
                context,
                debtMoney,
                userId,
                role,
                targetUser
        );

        debtRepository.save(newQuickDebt);

        return newQuickDebt;
    }
}
