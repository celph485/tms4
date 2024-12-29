package tms.nicerglobe;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletionException;

public class NicerGlobeClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NicerGlobeClient.class);

    private final HttpClient client;
    private final URI uri;

    private NicerGlobeClient(final URI uri) {
        this.uri = uri;
        this.client = HttpClient.newHttpClient();
    }

    public static NicerGlobeClient getInstance(final URI uri) {
        return new NicerGlobeClient(uri);
    }

    public void sendData(final List<Payload> payloads) {
        final var requests = payloads.stream()
                .map(this::toJson)
                .map(this::createRequest)
                .toList();
        LOGGER.debug("Total {} requests", CollectionUtils.size(requests));
        requests.parallelStream().forEach(req -> client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAcceptAsync(res -> LOGGER.debug("Status: {}, Response: {}", res.statusCode(), res.body())
        ));
    }

    private String toJson(final Payload payload) {
        try {
            return JsonMapper.getInstance()
                    .objectMapper()
                    .writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Unable to create json object for {}", payload);
            LOGGER.error("Error", e);
            throw new CompletionException(e);
        }
    }

    private HttpRequest createRequest(final String jsonPayload) {
        return HttpRequest
                .newBuilder()
                .uri(this.uri)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
    }
}
