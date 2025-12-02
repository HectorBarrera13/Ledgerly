package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.usecase.contract.CreateQuickDebt;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.debts.domain.vo.Role;
import toast.appback.src.debts.domain.vo.TargetUser;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.Name;
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
    public QuickDebtView execute(CreateQuickDebtCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFound(command.userId()));
        Result<DebtMoney, DomainError> moneyResult = DebtMoney.create(command.currency(), command.amount());
        Result<Context, DomainError> contextResult = Context.create(command.purpose(), command.description());
        Result<Role, DomainError> roleResult = Role.create(command.role());
        Result<TargetUser, DomainError> targetUserResult = TargetUser.create(command.targetUserName());
        Result<Void, DomainError> validationResult = Result.empty();
        validationResult.collect(moneyResult);
        validationResult.collect(contextResult);
        validationResult.collect(roleResult);
        validationResult.collect(targetUserResult);
        validationResult.ifFailureThrows(CreationDebtException::new);

        DebtMoney debtMoney = moneyResult.get();
        Context context = contextResult.get();
        Role role = roleResult.get();
        TargetUser targetUser = targetUserResult.get();
        UserId userId = user.getUserId();

        Name userName = user.getName();

        UserSummaryView userSummary = new UserSummaryView(
                userId.getValue(),
                userName.getFirstName(),
                userName.getLastName()
        );

        QuickDebt debt = QuickDebt.create(
                context,
                debtMoney,
                userId,
                role,
                targetUser
        );

        debtRepository.save(debt);

        return new QuickDebtView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount(),
                debt.getDebtMoney().getCurrency(),
                debt.getStatus().toString(),
                userSummary,
                debt.getRole().getRole(),
                debt.getTargetUser().getName()
        );
    }
}
