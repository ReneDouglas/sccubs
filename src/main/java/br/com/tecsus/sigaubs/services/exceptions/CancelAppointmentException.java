package br.com.tecsus.sigaubs.services.exceptions;

public class CancelAppointmentException extends Exception {

    public CancelAppointmentException(String message) {
        super(message);
    }

    public CancelAppointmentException(String message, Throwable cause) {
        super(message, cause);
    }

}
