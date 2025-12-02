package toast.appback.src.groups.application;

import org.junit.jupiter.api.Test;
import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.usecase.implementation.EditGroupUseCase;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditGroupTest {

    @Test
    void testEditGroupSuccessfully() {
        GroupRepository groupRepository = mock(GroupRepository.class);
        EditGroupUseCase useCase = new EditGroupUseCase(groupRepository);

        GroupId groupId = GroupId.generate();
        Group group = mock(Group.class);

        // Simula que el grupo existe en BD
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Nueva información válida del grupo
        GroupInformation updatedInfo = GroupInformation.load("Nuevo Nombre", "Nueva descripción");

        // Simulamos que después de la edición,
        // el grupo regresa la nueva información
        when(group.getGroupInformation()).thenReturn(updatedInfo);
        when(group.getId()).thenReturn(groupId);
        when(group.getCreatedAt()).thenReturn(Instant.now());

        EditGroupCommand command = new EditGroupCommand(
                groupId,
                "Nuevo Nombre",
                "Nueva descripción"
        );

        GroupView result = useCase.execute(command);

        // Validaciones
        assertEquals("Nuevo Nombre", result.name());
        assertEquals("Nueva descripción", result.description());
        assertEquals(groupId.getValue(), result.groupId());

        // Verifica que se haya buscado el grupo
        verify(groupRepository, times(1)).findById(groupId);

        // Verifica que se haya guardado el grupo editado
        verify(groupRepository, times(1)).save(group);

    }
}

