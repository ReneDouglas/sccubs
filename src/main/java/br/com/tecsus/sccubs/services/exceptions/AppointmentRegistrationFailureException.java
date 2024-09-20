package br.com.tecsus.sccubs.services.exceptions;

public class AppointmentRegistrationFailureException extends Exception {

    public AppointmentRegistrationFailureException(String message) {
        super(message);
    }

    public AppointmentRegistrationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
