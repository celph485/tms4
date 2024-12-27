package tms.nicerglobe;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NicerGlobeClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NicerGlobeClient.class);

    private final HttpClient client;
    private final URI uri;

    private NicerGlobeClient(final URI uri){
        this.uri = uri;
        this.client = HttpClient.newHttpClient();
    }

    public void sendData(final Payload payload)  {
        try {
            final var payloadString = toJson(payload);
            LOGGER.debug("NicerGlobe Payload: {}",payloadString);
            final var req = createRequest(payloadString);
            final var future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            future.thenAcceptAsync(res -> LOGGER.debug("Status: {}, Response: {}", res.statusCode(), res.body()));
        }catch (Exception e){
            LOGGER.error("Error while send data to NicerGlobe", e);
        }
    }

    public static NicerGlobeClient getInstance(final URI uri){
        return new NicerGlobeClient(uri);
    }

    private String toJson(final Payload payload) throws JsonProcessingException {
        return JsonMapper.getInstance()
                .objectMapper()
                .writeValueAsString(payload);
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
