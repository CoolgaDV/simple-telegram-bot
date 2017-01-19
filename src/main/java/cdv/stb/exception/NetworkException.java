package cdv.stb.exception;

/**
 * @author Dmitry Coolga
 *         19.01.2017 08:13
 */
public class NetworkException extends RuntimeException {

    public NetworkException(String method, Throwable cause) {
        super("API method: " + method, cause);
    }

}
