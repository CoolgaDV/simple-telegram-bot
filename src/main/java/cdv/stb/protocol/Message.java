package cdv.stb.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:18
 */
public class Message {

    private final long messageId;
    private final From from;
    private final Chat chat;
    private final long date;
    private final String text;

    @JsonCreator
    public Message(@JsonProperty("message_id") long messageId,
                   @JsonProperty("from") From from,
                   @JsonProperty("chat") Chat chat,
                   @JsonProperty("date") long date,
                   @JsonProperty("text") String text) {
        this.messageId = messageId;
        this.from = from;
        this.chat = chat;
        this.date = date;
        this.text = text;
    }

    public long getMessageId() {
        return messageId;
    }

    public From getFrom() {
        return from;
    }

    public Chat getChat() {
        return chat;
    }

    public long getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

}
