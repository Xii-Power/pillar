package com.xii.pillar.schema;

public class PException extends Exception{

    private String code;
    private String message;

    public PException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public PException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public PException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public PException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
