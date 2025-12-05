package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra el usuario receptor de una solicitud.
 */
public class ReceiverNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "receiver with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "the user who received the friend request was not found.";
    private final UUID userId;

    public ReceiverNotFound(UserId userId) {
        super(String.format(MESSAGE_TEMPLATE, userId.getValue()), FRIENDLY_MESSAGE);
        this.userId = userId.getValue();
    }

    /**
     * @return Identificador del usuario receptor que no fue encontrado.
     */
    public UserId getUserId() {
        return UserId.load(userId);
    }
}
