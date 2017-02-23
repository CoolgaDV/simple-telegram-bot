package cdv.stb.telegram.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:18
 */
public class Result {

    private final long updateId;
    private final Message message;

    @JsonCreator
    public Result(@JsonProperty("update_id") long updateId,
                  @JsonProperty("message") Message message) {
        this.updateId = updateId;
        this.message = message;
    }

    public long getUpdateId() {
        return updateId;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "updateId=" + updateId +
                ", message=" + message +
                '}';
    }

}
