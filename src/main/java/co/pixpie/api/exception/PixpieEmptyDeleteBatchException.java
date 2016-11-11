package co.pixpie.api.exception;

/**
 * Created by accord on 11/9/16.
 */
public class PixpieEmptyDeleteBatchException extends Exception {

    public PixpieEmptyDeleteBatchException(String s) {
        super(s);
    }

    public PixpieEmptyDeleteBatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PixpieEmptyDeleteBatchException(Throwable cause) {
        super(cause);
    }

}
