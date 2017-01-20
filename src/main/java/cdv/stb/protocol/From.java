package cdv.stb.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:17
 */
public class From {

    private final long id;
    private final String firstName;
    private final String secondName;
    private final String userName;

    @JsonCreator
    public From(@JsonProperty("id") long id,
                @JsonProperty("first_name") String firstName,
                @JsonProperty("last_name") String secondName,
                @JsonProperty("username") String userName) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.userName = userName;
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

}
