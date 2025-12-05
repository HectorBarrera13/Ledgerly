package toast.appback.src.groups.application.port;

import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

/**
 * Puerto de lectura para obtener deudas relacionadas con un grupo.
 */
public interface GroupDebtReadRepository {
    /**
     * Lista las deudas de un usuario en un grupo (page-based).
     */
    PageResult<DebtBetweenUsersView, UUID> findUserDebtsByGroupId(GroupId groupId, UserId userId, String role, String status, PageRequest pageRequest);

    /**
     * Paginación basada en cursor para las deudas de un usuario en un grupo.
     */
    PageResult<DebtBetweenUsersView, UUID> findUserDebtsByGroupIdAfterCursor(GroupId groupId, UserId userId, String role, String status, CursorRequest<UUID> cursorRequest);

}
