package cdv.stb.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Dmitry Coolga
 *         20.01.2017 08:18
 */
public class Response {

    private final boolean succeeded;
    private final List<Result> results;

    @JsonCreator
    public Response(@JsonProperty("ok") boolean succeeded,
                    @JsonProperty("result") List<Result> results) {
        this.succeeded = succeeded;
        this.results = results;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public List<Result> getResults() {
        return results;
    }

}
