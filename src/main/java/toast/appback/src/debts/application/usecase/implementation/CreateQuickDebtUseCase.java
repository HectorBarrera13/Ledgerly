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
 * Caso de uso para crear una QuickDebt.
 *
 * Tipo: Application Service dentro de Clean Architecture.
 *
 * Responsabilidades:
 *  - Validar existencia del usuario
 *  - Construir los Value Objects (DebtMoney, Context, Role, TargetUser)
 *  - Validarlos mediante Result<T,E>
 *  - Crear una QuickDebt (entidad del dominio)
 *  - Persistirla en el repositorio
 *  - Retornar un DTO de lectura (QuickDebtView)
 *
 * El caso de uso NO contiene lógica de negocio,
 * solo orquesta las llamadas entre repositorios, dominio y DTOs.
 */
public class CreateQuickDebtUseCase implements CreateQuickDebt {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;

    /**
     * Constructor inyectando dependencias (DIP).
     */
    public CreateQuickDebtUseCase(UserRepository userRepository, DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
    }

    /**
     * Flujo del caso de uso:
     *
     * 1. Verificar que el usuario exista
     * 2. Crear y validar los Value Objects: DebtMoney, Context, Role, TargetUser
     * 3. Si alguno falla, lanzar CreationDebtException
     * 4. Crear la entidad QuickDebt mediante el método estático del dominio
     * 5. Guardar la deuda
     * 6. Construir y retornar el DTO QuickDebtView
     */
    @Override
    public QuickDebtView execute(CreateQuickDebtCommand command) {

        // 1) Usuario debe existir
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFound(command.userId()));

        // 2) Construcción y validación de Value Objects
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

        // 4) Crear la entidad QuickDebt
        QuickDebt debt = QuickDebt.create(
                context,
                debtMoney,
                userId,
                role,
                targetUser
        );

        // 5) Persistencia
        debtRepository.save(debt);

        // 6) Retornar DTO
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