package toast.appback.src.debts.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.exceptions.AcceptDebtException;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsersStatus;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Caso de uso para que el deudor rechace una deuda antes de ser aceptada.
 *
 * Patrón: Application Service — Clean Architecture.
 *
 * Responsabilidades:
 *  1. Recuperar la entidad DebtBetweenUsers desde el repositorio
 *  2. Validar autorización (solo el deudor puede rechazar la deuda)
 *  3. Recuperar información del deudor y acreedor (para DTO de salida)
 *  4. Ejecutar la lógica de dominio: debt.reject()
 *  5. Manejar errores del dominio
 *  6. Persistir cambios
 *  7. Publicar eventos de dominio generados por la entidad
 *  8. Construir y retornar un DTO de lectura (DebtBetweenUsersView)
 *
 * Nota: este caso de uso NO implementa reglas de negocio.
 * Solo orquesta decisiones del dominio y repositorios.
 */
public class RejectDebtUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Dependencias inyectadas mediante constructor (DIP).
     */
    public RejectDebtUseCase(
            DebtRepository debtRepository,
            UserRepository userRepository,
            DomainEventBus domainEventBus
    ) {
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Flujo principal del caso de uso:
     *
     * 1. Buscar la deuda → lanzar DebtNotFound si no existe
     * 2. Validar que el actor sea el deudor
     * 3. Constuir los DTOs (deudor y acreedor)
     * 4. Llamar a debt.reject() (reglas del dominio)
     * 5. Si falla → lanzar AcceptDebtException
     * 6. Guardar cambios en repositorio
     * 7. Publicar eventos
     * 8. Retornar el DTO DebtBetweenUsersView
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Recuperar deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        // 2) Autorización: solo el deudor puede rechazar la deuda
        boolean isActorTheDebtor = command.actorId().equals(debt.getDebtorId());
        if (!isActorTheDebtor) {
            throw new UnauthorizedActionException("User is not the creditor");
        }

        // 3) Recuperar usuarios
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // Construir DTO de usuario deudor
        Name debtorName = debtor.getName();
        UserSummaryView debtorSummary = new UserSummaryView(
                debtor.getUserId().getValue(),
                debtorName.getFirstName(),
                debtorName.getLastName()
        );

        // Construir DTO de usuario acreedor
        Name creditorName = creditor.getName();
        UserSummaryView creditorSummary = new UserSummaryView(
                creditor.getUserId().getValue(),
                creditorName.getFirstName(),
                creditorName.getLastName()
        );

        // 4) Lógica de dominio (rechazar deuda)
        Result<Void, DomainError> result = debt.reject();

        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 5) Persistir cambios
        debtRepository.save(debt);

        // 6) Publicar eventos
        domainEventBus.publishAll(debt.pullEvents());

        // 7) Retornar DTO final
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
