package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.CreationDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.EditDebtException;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsers;
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
 * Caso de uso para editar una DebtBetweenUsers.
 *
 * Tipo: Application Service, parte de la capa de Aplicación en Clean Architecture.
 *
 * Responsabilidades:
 *  - Recuperar la deuda desde el repositorio
 *  - Crear y validar los Value Objects (nuevo Context & nuevo DebtMoney)
 *  - Ejecutar reglas de dominio para editar la deuda
 *  - Manejar fallos de dominio (Status no permite edición)
 *  - Recuperar información del deudor y acreedor
 *  - Persistir cambios y publicar eventos
 *  - Retornar un DTO de lectura (DebtBetweenUsersView)
 *
 * Característica clave:
 *  El caso de uso NO contiene reglas de negocio. Solo orquesta flujos en base al dominio.
 */
public class EditDebtBetweenUsersUseCase implements EditDebtBetweenUsers {

    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;
    private final UserRepository userRepository;

    /**
     * Las dependencias se inyectan por constructor (DIP).
     */
    public EditDebtBetweenUsersUseCase(
            DebtRepository debtRepository,
            DomainEventBus domainEventBus,
            UserRepository userRepository
    ) {
        this.debtRepository = debtRepository;
        this.domainEventBus = domainEventBus;
        this.userRepository = userRepository;
    }

    /**
     * Flujo del caso de uso:
     *
     * 1. Buscar la deuda → lanzar DebtNotFound si no existe
     * 2. Crear & validar nuevos Value Objects
     * 3. Intentar editar deuda con reglas de dominio
     * 4. Si falla edición → lanzar EditDebtException
     * 5. Recuperar debtor & creditor para el DTO final
     * 6. Guardar cambios
     * 7. Publicar eventos
     * 8. Retornar vista (DTO)
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtCommand command) {

        // 1) Recuperar la deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Nuevos Value Objects con validación
        DebtMoney newMoney = DebtMoney.create(command.newCurrency(), command.newAmount())
                .orElseThrow(CreationDebtException::new);

        Context newContext = Context.create(command.newPurpose(), command.newDescription())
                .orElseThrow(CreationDebtException::new);

        // 3) Reglas de dominio → editar dinero y contexto
        Result<Void, DomainError> editMoneyResult = debt.editDebtMoney(newMoney);
        Result<Void, DomainError> editContextResult = debt.editContext(newContext);

        // Agrupar posibles errores de dominio
        Result<Void, DomainError> updateResult = Result.empty();
        updateResult.collect(editMoneyResult);
        updateResult.collect(editContextResult);

        // 4) Si falla edición (ej. deuda NO está PENDING)
        if (updateResult.isFailure()) {
            throw new EditDebtException(updateResult.getErrors());
        }

        // 5) Recuperar debtor y creditor (para DTO final)
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        Name debtorName = debtor.getName();
        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(),
                debtorName.getFirstName(),
                debtorName.getLastName()
        );

        Name creditorName = creditor.getName();
        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(),
                creditorName.getFirstName(),
                creditorName.getLastName()
        );

        // 6) Guardar cambios definitivos
        debtRepository.save(debt);

        // 7) Publicar eventos generados
        domainEventBus.publishAll(debt.pullEvents());

        // 8) Retornar DTO final
        return new DebtBetweenUsersView(
                debt.getId().getValue(),
                debt.getContext().getPurpose(),
                debt.getContext().getDescription(),
                debt.getDebtMoney().getAmount(),
                debt.getDebtMoney().getCurrency(),
                debt.getStatus().name(),
                debtorSummary,
                creditorSummary
        );
    }
}
