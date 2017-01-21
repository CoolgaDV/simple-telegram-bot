package cdv.stb.exception;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:55
 */
public class RequestFailureException extends RuntimeException {

    public RequestFailureException(String message) {
        super("Response message text: " + message);
    }

}
