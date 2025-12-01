package toast.appback.src.debts.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsers;
import toast.appback.src.users.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EditDebtBetweenUsersService {
    private final EditDebtBetweenUsers editDebtBetweenUsers;

    @Transactional
    public DebtBetweenUsersView execute(EditDebtCommand command){
        return editDebtBetweenUsers.execute(command);
    }

}

