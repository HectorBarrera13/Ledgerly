package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

/**
 * Excepción lanzada cuando un usuario no se encuentra.
 */
public class UserNotFound extends ApplicationException {
    private static final String TEMPLATE = "user with id %s not found.";
    private static final String FRIENDLY = "user not found.";
    private final UUID userId;

    /**
     * Constructor de la excepción.
     *
     * @param userId el ID del usuario que no se encontró
     */
    public UserNotFound(UserId userId) {
        super(String.format(TEMPLATE, userId.getValue()), FRIENDLY);
        this.userId = userId.getValue();
    }

    /**
     * Obtiene el ID del usuario.
     *
     * @return el ID del usuario
     */
    public UserId getUserId() {
        return UserId.load(userId);
    }
}
