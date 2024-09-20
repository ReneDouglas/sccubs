package br.com.tecsus.sccubs.services.exceptions;

public class DuplicateAppointmentRegistrationException extends Exception {

    public DuplicateAppointmentRegistrationException(String message) {
        super(message);
    }

    public DuplicateAppointmentRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
