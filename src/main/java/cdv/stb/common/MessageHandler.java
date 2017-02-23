package cdv.stb.common;

import cdv.stb.telegram.protocol.Message;

/**
 * Message handler. If messages is matched it performs some action.
 *
 * @author Dmitry Coolga
 *         05.02.2017 11:04
 */
public interface MessageHandler {

    boolean match(Message message);

    void handle(Message message);

}
