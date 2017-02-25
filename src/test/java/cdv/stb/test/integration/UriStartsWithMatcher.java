package cdv.stb.test.integration;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.net.URI;

/**
 * Matches {@link URI} values by prefix
 *
 * @author Dmitry Coolga
 *         25.02.2017 09:48
 */
class UriStartsWithMatcher extends BaseMatcher<URI> {

    private final String prefix;

    UriStartsWithMatcher(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean matches(Object item) {
        return item instanceof URI && item.toString().startsWith(prefix);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("URI starts with (\"" + prefix + "\")");
    }

}
