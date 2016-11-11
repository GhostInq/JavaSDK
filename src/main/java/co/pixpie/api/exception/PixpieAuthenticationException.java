package co.pixpie.api.exception;

/**
 * Created by dmytro.radchenko on 2/12/16.
 */
public class PixpieAuthenticationException extends Exception {

    public PixpieAuthenticationException(String s) {
        super(s);
    }

    public PixpieAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PixpieAuthenticationException(Throwable cause) {
        super(cause);
    }
}
