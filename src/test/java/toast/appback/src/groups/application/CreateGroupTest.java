package toast.appback.src.groups.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.application.exceptions.CreationGroupException;
import toast.appback.src.groups.application.usecase.implementation.CreateGroupUseCase;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateGroupTest {

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private CreateGroupUseCase useCase;

    @BeforeEach
    void setup() {
        groupRepository = mock(GroupRepository.class);
        userRepository = mock(UserRepository.class);
        memberRepository = mock(MemberRepository.class);

        useCase = new CreateGroupUseCase(groupRepository, userRepository, memberRepository);
    }

    @Test
    void testCreateGroupSuccessfully() {
        UserId creatorId = UserId.generate();
        User creator = mock(User.class);
        when(creator.getUserId()).thenReturn(creatorId);

        when(userRepository.findById(creatorId))
                .thenReturn(Optional.of(creator));

        // OJO: respeta el orden real del constructor de CreateGroupCommand
        CreateGroupCommand command = new CreateGroupCommand(
                "Grupo X",
                "Descripción de prueba",
                creatorId
        );

        Group result = useCase.execute(command);

        assertNotNull(result);
        assertEquals("Grupo X", result.getGroupInformation().getName());
        assertEquals("Descripción de prueba", result.getGroupInformation().getDescription());
        assertEquals(creatorId, result.getCreatorId());

        // Verificamos que sí se haya guardado el grupo
        verify(groupRepository, times(1)).save(result);
        // Y que haya buscado al creador
        verify(userRepository, times(1)).findById(creatorId);
    }

    @Test
    void testFailsWhenCreatorNotFound() {
        UserId creatorId = UserId.generate();

        when(userRepository.findById(creatorId))
                .thenReturn(Optional.empty());

        CreateGroupCommand command = new CreateGroupCommand(

                "Grupo X", "Descripción", creatorId
        );

        assertThrows(UserNotFound.class, () -> useCase.execute(command));
    }

    @Test
    void testFailsWhenGroupInformationIsInvalid() {
        UserId creatorId = UserId.generate();
        User creator = mock(User.class);

        when(userRepository.findById(creatorId))
                .thenReturn(Optional.of(creator));

        // Nombre inválido (vacío)
        CreateGroupCommand command = new CreateGroupCommand(
                 "", "Descripción", creatorId
        );

        assertThrows(CreationGroupException.class, () -> useCase.execute(command));
    }
}