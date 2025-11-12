package toast.appback.src.shared.application;

public class ApplicationException extends RuntimeException {
    private final String friendlyMessage;

    public ApplicationException(String message) {
        super(message);
        this.friendlyMessage = "An unexpected error occurred. Please try again later.";
    }

    public ApplicationException(String message, String friendlyMessage) {
        super(message);
        this.friendlyMessage = friendlyMessage;
    }

    public String getFriendlyMessage() {
        return friendlyMessage;
    }
}
