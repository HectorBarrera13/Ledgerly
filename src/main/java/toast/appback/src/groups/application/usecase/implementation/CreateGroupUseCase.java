package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.application.exceptions.CreationGroupException;
import toast.appback.src.groups.application.usecase.contract.CreateGroup;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Implementación del caso de uso para crear un nuevo grupo.
 * <p>
 * Responsabilidades:
 * - Validar la información del grupo mediante {@link GroupInformation}.
 * - Verificar que el usuario creador exista.
 * - Persistir la entidad {@link Group} y devolverla.
 */
public class CreateGroupUseCase implements CreateGroup {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public CreateGroupUseCase(
            GroupRepository groupRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ejecuta la creación de un grupo.
     *
     * @param command Comando con los campos necesarios para crear el grupo (name, description, creatorId).
     * @return {@link Group} entidad creada y persistida.
     * @throws UserNotFound           Si el usuario creador no existe.
     * @throws CreationGroupException Si la validación de {@link GroupInformation} falla.
     */
    @Override
    public Group execute(CreateGroupCommand command) {
        User creator = userRepository.findById(command.creatorId())
                .orElseThrow(() -> new UserNotFound(command.creatorId())); // Valida creador

        Result<GroupInformation, DomainError> groupInfoResult = GroupInformation.create(
                command.name(),
                command.description()
        ); // Valida información del grupo

        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(groupInfoResult); // Acumula errores

        emptyResult.ifFailureThrows(CreationGroupException::new); // Lanza si hubo fallos

        GroupInformation groupInformation = groupInfoResult.get();
        Group group = Group.create(
                groupInformation,
                creator.getUserId()
        ); // Crea grupo con info validada y creador

        groupRepository.save(group); // Persiste grupo

        return group; // Devuelve grupo creado
    }
}