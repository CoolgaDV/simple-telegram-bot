package cdv.stb;

import cdv.stb.exception.NetworkException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Consumer;

/**
 * @author Dmitry Coolga
 *         19.01.2017 07:49
 */
public class TelegramApiClient {

    private final RestTemplate rest = new RestTemplate();

    private final String token;

    public TelegramApiClient(String token) {
        this.token = token;
    }

    public String getUpdates(int timeout, long offset) {
        return sendRequest("getUpdates", url -> {
            url.queryParam("timeout", String.valueOf(timeout));
            url.queryParam("offset", String.valueOf(offset));
        });
    }

    public String sendMessage(String message, long chatId) {
        return sendRequest("sendMessage", url -> {
                url.queryParam("chat_id", String.valueOf(chatId));
                url.queryParam("text", message);
        });
    }

    private String sendRequest(String method, Consumer<UriComponentsBuilder> urlCustomizer) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                    "https://api.telegram.org/bot" + token + "/" + method);
            urlCustomizer.accept(builder);
            ResponseEntity<String> response = rest.getForEntity(
                    builder.build().encode().toUri(),
                    String.class);
            return response.getBody();
        } catch (Exception ex) {
            throw new NetworkException(method, ex);
        }
    }

}
