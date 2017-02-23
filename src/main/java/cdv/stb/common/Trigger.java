package cdv.stb.common;

import cdv.stb.telegram.protocol.Message;

/**
 * Message trigger. If messages is matched it fires some action.
 *
 * @author Dmitry Coolga
 *         05.02.2017 11:04
 */
public interface Trigger {

    boolean match(Message message);

    void fire(Message message);

}
