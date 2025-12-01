package toast.appback.src.users.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.usecase.contract.EditUser;

@Service
@RequiredArgsConstructor
public class EditUserService {
    private final EditUser editUser;

    @Transactional
    public UserView execute(EditUserCommand command) {
        return editUser.execute(command);
    }
}
