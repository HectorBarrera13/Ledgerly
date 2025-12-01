package toast.appback.src.debts.application.port.services;

import java.util.UUID;

public interface UserQueryService {
    String getUserNameById(UUID userId);
}
