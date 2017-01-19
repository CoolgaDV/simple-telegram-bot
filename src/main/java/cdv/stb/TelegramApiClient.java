package cdv.stb;

import cdv.stb.exception.NetworkException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> parameters = new HashMap<>();
        parameters.put("timeout", String.valueOf(timeout));
        parameters.put("offset", String.valueOf(offset));
        return sendRequest("getUpdates", parameters);
    }

    public String sendMessage(String message, long chatId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("chat_id", String.valueOf(chatId));
        parameters.put("text", message);
        return sendRequest("sendMessage", parameters);
    }

    private String sendRequest(String method, Map<String, String> queryParameters) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<String> response = rest.exchange(
                    "https://api.telegram.org/bot" + token + "/" + method,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class,
                    queryParameters);
            return response.getBody();
        } catch (Exception ex) {
            throw new NetworkException(method, ex);
        }
    }

}
