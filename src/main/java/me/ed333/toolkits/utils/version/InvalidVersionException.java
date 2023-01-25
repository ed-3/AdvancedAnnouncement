package me.ed333.toolkits.utils.version;

public class InvalidVersionException extends Exception {

    public InvalidVersionException(String msg) {
        super(msg);
    }

    public InvalidVersionException(Throwable throwable) {
        super(throwable);
    }

    public InvalidVersionException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
