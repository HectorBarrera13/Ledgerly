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

    @Override
    public List<DebtBetweenUsersView> execute(AddGroupDebtCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + command.groupId())); // Valida grupo existente

        User creditor = userRepository.findById(command.creditorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + command.creditorId())); // Valida acreedor

        List<DebtBetweenUsersView> debts = new ArrayList<>();

        for (var debtorCommand : command.debtors()) {
            User debtor = userRepository.findById(debtorCommand.debtorId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + debtorCommand.debtorId())); // Valida deudor

            CreateDebtBetweenUsersCommand debtCommand = new CreateDebtBetweenUsersCommand(
                    command.purpose(),
                    command.description(),
                    command.currency(),
                    debtorCommand.amount(),
                    debtorCommand.debtorId(),
                    command.creditorId()
            ); // Comando para deuda individual

            DebtBetweenUsersView newDebt = createDebtBetweenUsers.execute(debtCommand); // Crea deuda

            GroupDebt groupDebt = GroupDebt.create(
                    group.getId(),
                    DebtId.load(newDebt.debtId())
            ); // Relación deuda–grupo

            groupDebtRepository.save(groupDebt); // Persiste relación

            debts.add(newDebt); // Agrega resultado
        }

        return debts; // Deudas creadas
    }
}

