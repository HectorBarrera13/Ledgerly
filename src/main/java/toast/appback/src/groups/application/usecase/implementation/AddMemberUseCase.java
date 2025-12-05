package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.application.exceptions.AddMemberException;
import toast.appback.src.groups.application.exceptions.GroupNotFound;
import toast.appback.src.groups.application.usecase.contract.AddMember;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso que agrega miembros a un grupo existente.
 * <p>
 * Responsabilidades principales:
 * - Verificar que el grupo exista.
 * - Verificar que el actor que realiza la acción exista y tenga permisos (actualmente, solo el creador puede agregar miembros).
 * - Validar cada usuario a agregar y persistir la membresía si no existe (evitar duplicados).
 * - Devolver las vistas de usuario de los miembros que realmente fueron añadidos.
 */
public class AddMemberUseCase implements AddMember {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public AddMemberUseCase(
            GroupRepository groupRepository,
            MemberRepository memberRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ejecuta la operación de agregar miembros al grupo.
     *
     * @param command Comando con `groupId`, `actorId` y la lista `membersId` a agregar.
     * @return Lista de {@link UserView} representando los miembros que fueron efectivamente añadidos.
     * @throws GroupNotFound      Si el grupo indicado no existe.
     * @throws UserNotFound       Si el actor o alguno de los usuarios a agregar no existe.
     * @throws AddMemberException Si el actor no tiene permisos para agregar miembros u ocurre otra regla de negocio.
     *                            <p>
     *                            Notas:
     *                            - Si un miembro ya existe en el grupo, se omite y no se incluye en la lista de retorno.
     */
    @Override
    public List<UserView> execute(AddMemberCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new GroupNotFound(command.groupId())); // Valida grupo

        User actor = userRepository.findById(command.actorId())
                .orElseThrow(() -> new UserNotFound(command.actorId())); // Valida actor

        boolean isActorCreator = actor.getUserId().equals(group.getCreatorId()); // Solo el creador puede agregar
        if (!isActorCreator) {
            throw new AddMemberException("Only the group creator can add members");
        }

        List<UserView> newMembers = new ArrayList<>();

        for (var memberId : command.membersId()) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new UserNotFound(memberId)); // Valida cada usuario

            GroupMember groupMember = GroupMember.create(group.getId(), user.getUserId()); // Crea membresía

            if (memberRepository.exists(group.getId(), groupMember)) {
                continue; // Evita duplicados
            }

            UserView memberView = new UserView(
                    user.getUserId().getValue(),
                    user.getName().getFirstName(),
                    user.getName().getLastName(),
                    user.getPhone().getValue()
            ); // DTO del nuevo miembro

            newMembers.add(memberView);
            memberRepository.save(groupMember); // Persiste miembro
        }

        return newMembers; // Devuelve solo miembros realmente agregados
    }
}
