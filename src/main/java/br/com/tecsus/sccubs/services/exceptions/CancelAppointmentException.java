package br.com.tecsus.sccubs.services.exceptions;

public class CancelAppointmentException extends Exception {

    public CancelAppointmentException(String message) {
        super(message);
    }

    public CancelAppointmentException(String message, Throwable cause) {
        super(message, cause);
    }

}
