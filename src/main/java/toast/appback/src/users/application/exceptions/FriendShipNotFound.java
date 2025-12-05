package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra una amistad entre dos usuarios.
 */
public class FriendShipNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "friendship between user %s and user %s not found.";
    private static final String FRIENDLY_MESSAGE = "friend not found.";
    private final UUID userAId;
    private final UUID userBId;

    /**
     * Constructor de la excepción.
     *
     * @param userAId ID del primer usuario.
     * @param userBId ID del segundo usuario.
     */
    public FriendShipNotFound(UserId userAId, UserId userBId) {
        super(
                String.format(MESSAGE_TEMPLATE, userAId.getValue(), userBId.getValue()),
                FRIENDLY_MESSAGE
        );
        this.userAId = userAId.getValue();
        this.userBId = userBId.getValue();

    }

    /**
     * Obtiene el ID del primer usuario.
     *
     * @return ID del primer usuario.
     */
    public UserId getUserAId() {
        return UserId.load(userAId);
    }

    /**
     * Obtiene el ID del segundo usuario.
     *
     * @return ID del segundo usuario.
     */
    public UserId getUserBId() {
        return UserId.load(userBId);
    }
}
