package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.application.communication.command.AddGroupDebtCommand;
import toast.appback.src.groups.application.usecase.contract.AddGroupDebt;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupDebtRepository;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupDebt;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para crear una deuda asociada a un grupo (convierte el comando grupal en múltiples deudas entre usuarios).
 * <p>
 * Flujo principal:
 * - Valida existencia del grupo y del usuario acreedor.
 * - Para cada deudor en el comando, valida el usuario, crea la deuda entre usuarios mediante el caso de uso
 * {@link CreateDebtBetweenUsers} y asocia la deuda creada al grupo mediante {@link GroupDebt}.
 */
public class CreateGroupDebtUseCase implements AddGroupDebt {
    private final GroupDebtRepository groupDebtRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CreateDebtBetweenUsers createDebtBetweenUsers;

    public CreateGroupDebtUseCase(GroupDebtRepository groupDebtRepository, GroupRepository groupRepository, UserRepository userRepository, CreateDebtBetweenUsers createDebtBetweenUsers) {
        this.groupDebtRepository = groupDebtRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.createDebtBetweenUsers = createDebtBetweenUsers;
    }

    /**
     * Ejecuta la creación de deudas grupales y retorna las vistas de las deudas creadas.
     *
     * @param command Comando que contiene groupId, creditorId, propósito, moneda y la lista de deudores con montos.
     * @return Lista de {@link DebtBetweenUsersView} con las deudas creadas y asociadas al grupo.
     * @throws IllegalArgumentException Si el grupo o algún usuario (acreedor o deudor) no existe.
     */
    @Override
    public List<DebtBetweenUsersView> execute(AddGroupDebtCommand command) {
        // Valida grupo existente
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + command.groupId()));

        // Valida acreedor
        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + command.creditorId()));

        List<DebtBetweenUsersView> debts = new ArrayList<>();

        //Para cada deudor, crea deuda con el creditor
        for (var debtorCommand : command.debtors()) {
            User debtor = userRepository.findById(debtorCommand.debtorId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + debtorCommand.debtorId()));

            //Crear el comando para crear deuda entre usuarios
            CreateDebtBetweenUsersCommand debtCommand = new CreateDebtBetweenUsersCommand(
                    command.purpose(),
                    command.description(),
                    command.currency(),
                    debtorCommand.amount(),
                    debtor.getUserId(),
                    creditor.getUserId()
            );

            DebtBetweenUsersView newDebt = createDebtBetweenUsers.execute(debtCommand);

            GroupDebt groupDebt = GroupDebt.create(
                    group.getId(),
                    DebtId.load(newDebt.debtId())
            );

            groupDebtRepository.save(groupDebt);

            debts.add(newDebt);
        }

        return debts;
    }
}
