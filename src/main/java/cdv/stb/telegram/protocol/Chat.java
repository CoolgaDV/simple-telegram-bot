package cdv.stb.telegram.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:17
 */
public class Chat {

    private final long id;
    private final String firstName;
    private final String secondName;
    private final String userName;
    private final String type;

    @JsonCreator
    public Chat(@JsonProperty("id") long id,
                @JsonProperty("first_name") String firstName,
                @JsonProperty("last_name") String secondName,
                @JsonProperty("username") String userName,
                @JsonProperty("type") String type) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.userName = userName;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getUserName() {
        return userName;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", userName='" + userName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
