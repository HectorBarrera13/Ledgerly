package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.exceptions.EditGroupException;
import toast.appback.src.groups.application.exceptions.GroupEditionException;
import toast.appback.src.groups.application.usecase.contract.EditGroup;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Implementación del caso de uso para editar la información básica de un grupo.
 * <p>
 * Reglas principales:
 * - Solo el creador del grupo puede editar su información.
 * - Valida la nueva información mediante {@link GroupInformation}.
 */
public class EditGroupUseCase implements EditGroup {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public EditGroupUseCase(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ejecuta la edición del grupo.
     *
     * @param command Comando con groupId, actorId y los nuevos campos (name, description).
     * @return {@link GroupView} vista pública con la información actualizada.
     * @throws IllegalArgumentException Si el grupo o el usuario actor no existen.
     * @throws EditGroupException       Si el actor no tiene permisos o la edición no cumple las reglas.
     */
    @Override
    public GroupView execute(EditGroupCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + command.groupId())); // Valida que el grupo exista

        User creator = userRepository.findById(command.actorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + command.actorId())); // Valida que el usuario exista

        if (!group.getCreatorId().equals(creator.getUserId())) {
            throw new EditGroupException("Only the group creator can edit the group.");
        }

        GroupInformation newInfo = GroupInformation.create(
                command.name(),
                command.description()
        ).orElseThrow(GroupEditionException::new); // Valida nueva información del grupo

        group.editGroupInformation(newInfo);

        groupRepository.save(group); // Persiste cambios

        return new GroupView(
                group.getId().getValue(),
                group.getCreatorId().getValue(),
                group.getGroupInformation().getName(),       // Usa la info del grupo
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        ); // Devuelve representación del grupo
    }
}
