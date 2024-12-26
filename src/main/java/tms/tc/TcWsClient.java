package tms.tc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.PropertiesLoader;

import java.net.URI;
import java.net.http.HttpClient;

import static tms.common.ConfigKey.TC_SERVER_URL;
import static tms.common.ConfigKey.TC_WS_ENDPOINT;

public final class TcWsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcWsClient.class);

    private final TcWsListener tcWsListener;

    private TcWsClient(final TcWsListener tcWsListener){
        this.tcWsListener = tcWsListener;
    }

    public void connect(final String sessionCookie) throws InterruptedException {
        final var wsUrlString = getWebSocketUrl();
        LOGGER.info("Connecting to {}", wsUrlString);
        final var wsUrl = URI.create(wsUrlString);
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .header("Cookie", sessionCookie)
                .buildAsync(wsUrl, tcWsListener);
        Thread.currentThread().join();
    }

    private String getWebSocketUrl(){
        final var props = PropertiesLoader.getInstance();
        final var serverUrl = props.getProperty(TC_SERVER_URL);
        final var wsEndpoint = props.getProperty(TC_WS_ENDPOINT);
        return StringUtils.replace(serverUrl, "http", "ws")+wsEndpoint;
    }
    public static TcWsClient getInstance (final TcWsListener tcWsListener){
        return new TcWsClient(tcWsListener);
    }
}
