package me.ed333.toolkits.utils.version;

public class ReleaseVersionSetException extends Exception {
    public ReleaseVersionSetException(String msg) {
        super(msg);
    }

    public ReleaseVersionSetException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}