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

/**
 * Implementación del caso de uso que crea una deuda rápida (QuickDebt).
 *
 * <p>Responsabilidades:
 * - Validar los Value Objects (monto, contexto, rol, target user).
 * - Comprobar que el usuario creador existe.
 * - Crear la entidad {@link QuickDebt}, persistirla y devolver una vista pública.
 */
public class CreateQuickDebtUseCase implements CreateQuickDebt {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;

    public CreateQuickDebtUseCase(UserRepository userRepository, DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }

    /**
     * Ejecuta la creación de una deuda rápida.
     *
     * @param command Comando con los datos necesarios para crear la deuda (propósito, descripción, moneda, monto, userId, rol, targetUserName).
     * @return {@link QuickDebtView} vista pública de la deuda creada.
     * @throws UserNotFound          Si el usuario creador no existe.
     * @throws CreationDebtException Si alguno de los Value Objects no cumple las reglas de validación.
     */
    @Override
    public QuickDebtView execute(CreateQuickDebtCommand command) {
        //Comprobar que el usuario existe
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFound(command.userId()));

        //Validar los VO del caso de uso
        Result<DebtMoney, DomainError> moneyResult =
                DebtMoney.create(command.currency(), command.amount());
        Result<Context, DomainError> contextResult =
                Context.create(command.purpose(), command.description());
        Result<Role, DomainError> roleResult =
                Role.create(command.role());
        Result<TargetUser, DomainError> targetUserResult =
                TargetUser.create(command.targetUserName());

        // Colección de errores
        Result<Void, DomainError> validationResult = Result.empty();
        validationResult.collect(moneyResult);
        validationResult.collect(contextResult);
        validationResult.collect(roleResult);
        validationResult.collect(targetUserResult);

        // Lanzar excepción si un VO es inválido
        validationResult.ifFailureThrows(CreationDebtException::new);

        // Obtener objetos validados
        DebtMoney debtMoney = moneyResult.get();
        Context context = contextResult.get();
        Role role = roleResult.get();
        TargetUser targetUser = targetUserResult.get();
        UserId userId = user.getUserId();

        // Preparar DTO del usuario creador
        Name userName = user.getName();

        UserSummaryView userSummary = new UserSummaryView(
                userId.getValue(),
                userName.getFirstName(),
                userName.getLastName()
        );

        QuickDebt newDebt = QuickDebt.create(
                context,
                debtMoney,
                userId,
                role,
                targetUser
        );

        debtRepository.save(newDebt);

        return new QuickDebtView(
                newDebt.getId().getValue(),
                newDebt.getContext().getPurpose(),
                newDebt.getContext().getDescription(),
                newDebt.getDebtMoney().getAmount(),
                newDebt.getDebtMoney().getCurrency(),
                newDebt.getStatus().toString(),
                userSummary,
                newDebt.getRole().getValue(),
                newDebt.getTargetUser().getName()
        );
    }
}