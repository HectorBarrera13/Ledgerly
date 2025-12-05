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
 * Implementación del caso de uso para crear una deuda entre dos usuarios registrados.
 *
 * <p>Responsabilidades:
 * - Comprobar existencia de deudor y acreedor.
 * - Validar Value Objects (contexto y monto).
 * - Crear la entidad {@link DebtBetweenUsers}, persistirla y publicar eventos de dominio.
 */
public class CreateDebtBetweenUsersUseCase implements CreateDebtBetweenUsers {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;

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
     * Ejecuta la creación de una deuda entre usuarios.
     *
     * @param command Comando con datos del propósito, monto y los IDs de los dos usuarios.
     * @return {@link DebtBetweenUsersView} vista pública de la deuda creada.
     * @throws DebtorNotFound        Si el usuario deudor no existe.
     * @throws CreditorNotFound      Si el usuario acreedor no existe.
     * @throws CreationDebtException Si alguno de los Value Objects no cumple las reglas de validación.
     */
    @Override
    public DebtBetweenUsersView execute(CreateDebtBetweenUsersCommand command) {
        //Comprobamos que existan los usuarios
        User debtor = userRepository.findById(command.debtorId())
                .orElseThrow(() -> new DebtorNotFound(command.debtorId().getValue()));
        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new CreditorNotFound(command.creditorId().getValue()));

        Name debtorName = debtor.getName();
        Name creditorName = creditor.getName();

        //Crear vistas resumen de usuarios para la respuesta
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

        // Validar VOs
        Result<Void, DomainError> validationResult = Result.empty();
        Result<Context, DomainError> contextResult =
                Context.create(command.purpose(), command.description());
        Result<DebtMoney, DomainError> debtMoneyResult =
                DebtMoney.create(command.currency(), command.amount());

        validationResult.collect(contextResult);
        validationResult.collect(debtMoneyResult);

        // Si algún VO falló, se lanza CreationDebtException
        validationResult.ifFailureThrows(CreationDebtException::new);

        Context context = contextResult.get();
        DebtMoney debtMoney = debtMoneyResult.get();

        DebtBetweenUsers newDebt = DebtBetweenUsers.create(
                context, debtMoney, command.debtorId(), command.creditorId()
        );

        debtRepository.save(newDebt);

        domainEventBus.publishAll(newDebt.pullEvents());

        return new DebtBetweenUsersView(
                newDebt.getId().getValue(),
                newDebt.getContext().getPurpose(),
                newDebt.getContext().getDescription(),
                newDebt.getDebtMoney().getAmount(),
                newDebt.getDebtMoney().getCurrency(),
                newDebt.getStatus().toString(),
                debtorSummary,
                creditorSummary
        );
    }
}
