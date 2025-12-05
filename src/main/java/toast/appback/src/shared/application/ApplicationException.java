package toast.appback.src.shared.application;

/**
 * Excepción base para errores en la capa de aplicación.
 * <p>
 * Proporciona un mensaje amigable para el usuario final.
 */
public class ApplicationException extends RuntimeException {
    private final String friendlyMessage;

    public ApplicationException(String message) {
        super(message);
        this.friendlyMessage = "Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.";
    }

    public ApplicationException(String message, String friendlyMessage) {
        super(message);
        this.friendlyMessage = friendlyMessage;
    }

    /**
     * Mensaje amigable para el usuario final.
     *
     * @return Mensaje amigable.
     */
    public String getFriendlyMessage() {
        return friendlyMessage;
    }
}
