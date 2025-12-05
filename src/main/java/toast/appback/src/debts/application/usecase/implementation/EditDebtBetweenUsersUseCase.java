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
 * Implementación del caso de uso para editar una deuda entre usuarios.
 *
 * <p>Responsabilidades:
 * - Validar los Value Objects nuevos.
 * - Aplicar la edición y persistir, publicando los eventos de dominio.
 */
public class EditDebtBetweenUsersUseCase implements EditDebtBetweenUsers {

    private final DebtRepository debtRepository;
    private final DomainEventBus domainEventBus;
    private final UserRepository userRepository;

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
     * Ejecuta la edición de una deuda entre usuarios.
     *
     * @param command Comando con el identificador de la deuda y los nuevos campos.
     * @return {@link DebtBetweenUsersView} vista actualizada.
     * @throws DebtNotFound      Si la deuda o los usuarios involucrados no se encuentran.
     * @throws EditDebtException Si alguna validación o regla de negocio impide la edición.
     */
    @Override
    public DebtBetweenUsersView execute(EditDebtCommand command) {
        //Comprobar si la deuda existe
        DebtBetweenUsers debt = debtRepository.findDebtBetweenUsersById(command.debtId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        //Comprobar si los usuarios existen
        User debtor = userRepository.findById(debt.getDebtorId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));
        User creditor = userRepository.findById(debt.getCreditorId())
                .orElseThrow(() -> new DebtNotFound(command.debtId().getValue()));

        //Crear nuevos VO de dinero y contexto
        DebtMoney newMoney = DebtMoney.create(command.newCurrency(), command.newAmount())
                .orElseThrow(CreationDebtException::new);

        Context newContext = Context.create(command.newPurpose(), command.newDescription())
                .orElseThrow(CreationDebtException::new);

        Result<Void, DomainError> editMoneyResult = debt.editDebtMoney(newMoney);
        Result<Void, DomainError> editContextResult = debt.editContext(newContext);

        //Recoger resultados de edición
        Result<Void, DomainError> updateResult = Result.empty();
        updateResult.collect(editMoneyResult);
        updateResult.collect(editContextResult);

        //Si hay errores, lanzar excepción
        if (updateResult.isFailure()) {
            throw new EditDebtException(updateResult.getErrors());
        }

        //Preparar vistas de usuario
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

        debtRepository.save(debt);

        domainEventBus.publishAll(debt.pullEvents());

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
