package toast.appback.src.groups.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.usecase.contract.EditGroup;

@Service
@RequiredArgsConstructor
public class EditGroupService {
    private final EditGroup editGroup;

    @Transactional
    public GroupView execute(EditGroupCommand command) {
        return editGroup.execute(command);
    }
}
