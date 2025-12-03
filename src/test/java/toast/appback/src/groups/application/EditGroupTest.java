package toast.appback.src.groups.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.usecase.implementation.EditGroupUseCase;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditGroupTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EditGroupUseCase useCase;

    @Test
    void testEditGroupSuccessfully() {
        // Arrange
        GroupId groupId = GroupId.generate();
        UserId creatorId = UserId.generate();
        Group group = mock(Group.class);
        User user = mock(User.class);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(user));
        when(user.getUserId()).thenReturn(creatorId);
        when(group.getCreatorId()).thenReturn(creatorId);

        GroupInformation updatedInfo = GroupInformation.load("Nuevo Nombre", "Nueva descripción");

        when(group.getId()).thenReturn(groupId);
        when(group.getGroupInformation()).thenReturn(updatedInfo);
        when(group.getCreatedAt()).thenReturn(Instant.now());

        EditGroupCommand command = new EditGroupCommand(
                groupId,
                creatorId,
                "Nuevo Nombre",
                "Nueva descripción"
        );

        // Act
        GroupView result = useCase.execute(command);

        // Assert
        assertEquals(groupId.getValue(), result.groupId());
        assertEquals("Nuevo Nombre", result.name());
        assertEquals("Nueva descripción", result.description());

        verify(groupRepository).findById(groupId);
        verify(groupRepository).save(group);
        verifyNoMoreInteractions(groupRepository);
    }
}

