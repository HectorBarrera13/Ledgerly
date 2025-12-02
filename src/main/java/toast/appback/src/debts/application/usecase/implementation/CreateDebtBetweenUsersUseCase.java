package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Caso de uso para crear una deuda entre dos usuarios (DebtBetweenUsers).
 *
 * Tipo: Application Service (Clean Architecture).
 *
 * Responsabilidades:
 *  - Validar existencia del deudor y acreedor
 *  - Construir los Value Objects (Context, DebtMoney)
 *  - Validar dichos Value Objects usando composición de Result
 *  - Crear la entidad del dominio mediante DebtBetweenUsers.create()
 *  - Persistir la nueva deuda
 *  - Publicar eventos de dominio registrados por la entidad
 *  - Retornar un DTO de solo lectura (DebtBetweenUsersView)
 */
public class CreateDebtBetweenUsersUseCase implements CreateDebtBetweenUsers {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Las dependencias se reciben por constructor, cumpliendo con DIP.
     */
    public CreateDebtBetweenUsersUseCase(
            UserRepository userRepository,
            DebtRepository debtRepository,
            DomainEventBus domainEventBus
    ) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Flujo del caso de uso:
     *
     * 1. Verificar que el deudor exista (DebtorNotFound)
     * 2. Verificar que el acreedor exista (CreditorNotFound)
     * 3. Crear y validar los Value Objects (Context y DebtMoney)
     * 4. Crear la entidad DebtBetweenUsers en estado PENDING
     * 5. Guardar la deuda creada
     * 6. Publicar eventos generados (como DebtCreated)
     * 7. Retornar la representación en DTO (DebtBetweenUsersView)
     */
    @Override
    public DebtBetweenUsersView execute(CreateDebtBetweenUsersCommand command) {

        // 1) Recuperar al deudor
        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));

        // 2) Recuperar al acreedor
        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

        // Obtener nombres
        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

        // Construcción de DTOs de resumen
        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(),
                debtorName.getFirstName(),
                debtorName.getLastName()
        );

        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(),
                creditorName.getFirstName(),
                creditorName.getLastName()
        );

        // 3) Validaciones de Value Objects usando Result<T,E>
        Result<Void, DomainError> validationResult = Result.empty();

        Result<Context, DomainError> contextResult =
                Context.create(command.purpose(), command.description());

        Result<DebtMoney, DomainError> debtMoneyResult =
                DebtMoney.create(command.currency(), command.amount());

        validationResult.collect(contextResult);
        validationResult.collect(debtMoneyResult);

        // Si algún VO falló, se lanza CreationDebtException
        validationResult.ifFailureThrows(CreationDebtException::new);

        // 4) Crear entidad del dominio
        Context context = contextResult.get();
        DebtMoney debtMoney = debtMoneyResult.get();

        DebtBetweenUsers debt = DebtBetweenUsers.create(
                context, debtMoney, command.debtorId(), command.creditorId()
        );

        // 5) Guardar cambios
        debtRepository.save(debt);

        // 6) Publicar eventos generados por el agregado (ej. DebtCreated)
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Retornar vista
        return new DebtBetweenUsersView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount(),
                debt.getDebtMoney().getCurrency(),
                debt.getStatus().toString(),
                debtorSummary,
                creditorSummary
        );
    }
}

