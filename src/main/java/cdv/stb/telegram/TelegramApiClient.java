package cdv.stb.telegram;

import cdv.stb.exception.MessageFormatException;
import cdv.stb.exception.NetworkException;
import cdv.stb.exception.RequestFailureException;
import cdv.stb.telegram.protocol.Response;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Client for integration with Telegram bot API
 *
 * @author Dmitry Coolga
 *         19.01.2017 07:49
 */
public class TelegramApiClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    private final String token;

    public TelegramApiClient(String token, RestTemplate restTemplate) {
        this.token = token;
        this.restTemplate = restTemplate;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Response getUpdates(int timeout, long offset) {
        return parseResponse(sendRequest("getUpdates", url -> {
            url.queryParam("timeout", String.valueOf(timeout));
            url.queryParam("offset", String.valueOf(offset));
        }));
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
            ResponseEntity<String> response = restTemplate.getForEntity(
                    builder.build().encode().toUri(),
                    String.class);
            return response.getBody();
        } catch (Exception ex) {
            throw new NetworkException(method, ex);
        }
    }

    private Response parseResponse(String source) {
        Response response;
        try {
            response = mapper.readValue(source, Response.class);
        } catch (IOException ex) {
            throw new MessageFormatException(source, ex);
        }
        if ( ! response.isSucceeded()) {
            throw new RequestFailureException(source);
        }
        return response;
    }

}
