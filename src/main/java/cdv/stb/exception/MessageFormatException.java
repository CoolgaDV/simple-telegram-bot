package cdv.stb.exception;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:55
 */
public class MessageFormatException extends RuntimeException {

    public MessageFormatException(String sourceMessage, Throwable cause) {
        super("Message text: " + sourceMessage, cause);
    }

}
