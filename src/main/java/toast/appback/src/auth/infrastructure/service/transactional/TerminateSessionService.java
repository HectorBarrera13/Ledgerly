package toast.appback.src.auth.infrastructure.service.transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;

@Service
@RequiredArgsConstructor
public class TerminateSessionService {
    private final TerminateSession terminateSession;

    @Transactional
    public void execute(String accessToken) {
        terminateSession.execute(accessToken);
    }
}
