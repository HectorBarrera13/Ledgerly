package toast.appback.src.users.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.usecase.contract.AddFriend;

@Service
@RequiredArgsConstructor
public class AddFriendService {
    private final AddFriend addFriend;

    @Transactional
    public FriendView execute(AddFriendCommand command) {
        return addFriend.execute(command);
    }
}
