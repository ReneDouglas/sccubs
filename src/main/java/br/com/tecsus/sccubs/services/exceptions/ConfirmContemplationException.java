package br.com.tecsus.sccubs.services.exceptions;

import org.hibernate.HibernateException;

import java.io.Serial;

public class ConfirmContemplationException extends HibernateException {

    @Serial
    private static final Long serialVersionUID = 1L;

    public ConfirmContemplationException(String message) {
        super(message);
    }

    public ConfirmContemplationException(String message, Throwable cause) {
        super(message, cause);
    }
}
