package br.com.tecsus.sigaubs.services.exceptions;

public class DuplicateAppointmentRegistrationException extends Exception {

    public DuplicateAppointmentRegistrationException(String message) {
        super(message);
    }

    public DuplicateAppointmentRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
