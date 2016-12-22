package me.matamor.ge.shared.identifier.exception;

public class IdentifierException extends Exception {

    private final ExceptionReason reason;

    public IdentifierException(ExceptionReason reason) {
        this.reason = reason;
    }

    public IdentifierException(ExceptionReason reason, String message) {
        super(message);

        this.reason = reason;
    }

    public IdentifierException(ExceptionReason reason, String message, Exception exception) {
        super(message, exception);

        this.reason = reason;
    }

    public ExceptionReason getReason() {
        return reason;
    }
}
