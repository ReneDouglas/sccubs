package br.com.tecsus.sigaubs.services.exceptions;

import java.io.Serial;

public class InvalidConfirmPasswordException extends Exception{

    @Serial
    private static final Long serialVersionUID = 1L;

    public InvalidConfirmPasswordException(String message) {
        super(message);
    }

    public InvalidConfirmPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
