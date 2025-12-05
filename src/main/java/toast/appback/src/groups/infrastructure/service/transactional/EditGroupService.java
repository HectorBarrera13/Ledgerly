package toast.appback.src.groups.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.usecase.contract.EditGroup;

/**
 * Envoltura transaccional para el caso de uso de edición de grupos.
 */
@Service
@RequiredArgsConstructor
public class EditGroupService {
    private final EditGroup editGroup;

    /**
     * Ejecuta la edición de grupo dentro de una transacción.
     *
     * @param command Comando con los datos de edición.
     * @return Vista pública del grupo actualizado.
     */
    @Transactional
    public GroupView execute(EditGroupCommand command) {
        return editGroup.execute(command);
    }
}
