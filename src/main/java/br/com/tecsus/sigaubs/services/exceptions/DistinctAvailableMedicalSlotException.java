package br.com.tecsus.sigaubs.services.exceptions;

public class DistinctAvailableMedicalSlotException extends Exception {

    public DistinctAvailableMedicalSlotException(String message) {
        super(message);
    }

    public DistinctAvailableMedicalSlotException(String message, Throwable cause) {
        super(message, cause);
    }

}
