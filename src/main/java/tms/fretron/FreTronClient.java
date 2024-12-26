package tms.fretron;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public final class FreTronClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreTronClient.class);

    private final HttpClient client;
    private final URI uri;

    private FreTronClient(final URI uri){
        this.uri = uri;
        this.client = HttpClient.newHttpClient();
    }

    public void sendData(final List<Payload> payloads)  {
        try {
            final var payloadString = toJson(payloads);
            LOGGER.debug("FreTron Payload: {}",payloadString);
            final var req = createRequest(payloadString);
            final var future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            future.thenAcceptAsync(res -> LOGGER.debug("Status: {}, Response: {}", res.statusCode(), res.body()));
        }catch (Exception e){
            LOGGER.error("Error while send data", e);
        }
    }

    public static FreTronClient getInstance(final URI uri){
        return new FreTronClient(uri);
    }

    private String toJson(final List<Payload> payloads) throws JsonProcessingException {
        return JsonMapper.getInstance()
                .objectMapper()
                .writeValueAsString(payloads);
    }

    private HttpRequest createRequest(final String jsonPayload){
        return HttpRequest
                .newBuilder()
                .uri(this.uri)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
    }
}
