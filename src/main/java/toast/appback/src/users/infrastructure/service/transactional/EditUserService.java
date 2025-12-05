package toast.appback.src.users.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.usecase.contract.EditUser;

/**
 * Envoltura transaccional para el caso de uso de edición de usuario.
 */
@Service
@RequiredArgsConstructor
public class EditUserService {
    private final EditUser editUser;

    /**
     * Ejecuta la edición de usuario dentro de una transacción.
     *
     * @param command Comando con los datos a editar.
     * @return Vista pública del usuario actualizado.
     */
    @Transactional
    public UserView execute(EditUserCommand command) {
        return editUser.execute(command);
    }
}
