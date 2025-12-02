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
 * Caso de uso para reenviar una deuda entre usuarios.
 *
 * Patrón: Application Service (Clean Architecture).
 *
 * Responsabilidades del caso de uso:
 *  1. Recuperar la deuda desde el repositorio
 *  2. Validar autorización: solo el acreedor puede reenviar la deuda
 *  3. Recuperar información del deudor y acreedor para construir DTO
 *  4. Ejecutar la lógica del dominio: debt.resend()
 *  5. Manejar los errores del dominio
 *  6. Guardar los cambios realizados en el agregado
 *  7. Publicar los eventos generados
 *  8. Retornar un DTO con el estado actualizado de la deuda
 *
 * Nota: el caso de uso no debe contener reglas de negocio,
 * únicamente orquestar la coordinación del dominio.
 */
public class ResendDebtUseCase implements EditDebtBetweenUsersStatus {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    /**
     * Inyección de dependencias mediante constructor,
     * siguiendo el principio de inversión de dependencias (DIP).
     */
    public ResendDebtUseCase(
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
     * 1. Buscar deuda (o lanzar error)
     * 2. Validar autorización: solo el acreedor puede reenviar
     * 3. Recuperar debtor y creditor
     * 4. Ejecutar lógica de dominio: debt.resend()
     * 5. Manejar errores del dominio
     * 6. Persistir cambios
     * 7. Publicar eventos
     * 8. Retornar DTO actualizado
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtStatusCommand command) {

        // 1) Recuperar deuda
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        // 2) Validación de autorización — solo el acreedor puede reenviar
        boolean isActorTheCreditor = command.actorId().equals(debt.getCreditorId());
        if (!isActorTheCreditor) {
            throw new UnauthorizedActionException("User is not the creditor");
        }

        // 3) Recuperar deudor y acreedor para armar el DTO
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtorNotFound(debt.getDebtorId().getValue()));

        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(debt.getCreditorId().getValue()));

        // Construcción de resúmenes para DTO
        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

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

        // 4) Lógica del dominio
        Result<Void, DomainError> result = debt.resend();

        // 5) Si falla → lanzar excepción de aplicación
        if (result.isFailure()) {
            throw new AcceptDebtException(result.getErrors());
        }

        // 6) Persistir cambios
        debtRepository.save(debt);

        // 7) Publicar eventos de dominio
        domainEventBus.publishAll(debt.pullEvents());

        // 8) Construir respuesta final
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
