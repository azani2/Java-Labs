package bg.sofia.uni.fmi.mjt.todoist;

public class CollaborationFileFormatException extends Exception {
    private static final String DEFAULT_ERROR_CODE = "";
    private final String errorCode;
    private final String errorMessage;

    public CollaborationFileFormatException(String message) {
        super(message);
        this.errorMessage = message;
        errorCode = DEFAULT_ERROR_CODE;
    }

    public CollaborationFileFormatException(String code, String message) {
        super(code + ": " + message);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public CollaborationFileFormatException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = DEFAULT_ERROR_CODE;
        this.errorMessage = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
