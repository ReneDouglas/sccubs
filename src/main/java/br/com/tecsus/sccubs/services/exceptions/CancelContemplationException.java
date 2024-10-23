package br.com.tecsus.sccubs.services.exceptions;

import org.hibernate.HibernateException;
import java.io.Serial;

public class CancelContemplationException extends HibernateException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CancelContemplationException(String message) {
        super(message);
    }

    public CancelContemplationException(String message, Throwable cause) {
        super(message, cause);
    }
}
