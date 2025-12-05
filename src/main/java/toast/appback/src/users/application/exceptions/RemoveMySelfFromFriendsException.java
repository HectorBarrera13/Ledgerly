package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

/**
 * Excepción lanzada cuando un usuario intenta eliminarse a sí mismo de su lista de amigos.
 */
public class RemoveMySelfFromFriendsException extends ApplicationException {
    private static final String MESSAGE = "cannot remove yourself from friends.";
    private static final String FRIENDLY = "you cannot remove yourself from your friends list.";

    public RemoveMySelfFromFriendsException() {
        super(MESSAGE, FRIENDLY);
    }
}
