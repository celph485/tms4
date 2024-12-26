package tms.tc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.PropertiesLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static tms.common.ConfigKey.*;

public final class TcAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcAuthService.class);

    private final String apiUrl;

    private final String username;

    private final String password;

    private final HttpClient httpClient;

    public TcAuthService() {
        final var props = PropertiesLoader.getInstance();
        this.apiUrl = props.getProperty(TC_SERVER_URL) + props.getProperty(TC_SESSION_API_ENDPOINT);
        this.username = props.getProperty(TC_USERNAME);
        this.password = props.getProperty(TC_PASSWORD);
        LOGGER.debug("Session Api Url: {}", apiUrl);
        LOGGER.debug("Username: {}", username);
        LOGGER.debug("Password: {}", password);
        this.httpClient = HttpClient.newHttpClient();
    }

    public String authenticate() throws IOException, InterruptedException {
        final var formBody = String.format("email=%s&password=%s", username, password);
        final var req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        final var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        LOGGER.info("HTTP response status code: {}", res.statusCode());
        if(res.statusCode() != 200){
            LOGGER.error("Authentication Error.{}",res.body());
            throw new RuntimeException("Authentication failed");
        }

        return  res.headers()
                .firstValue("Set-Cookie")
                .orElseThrow(() -> new RuntimeException("Session cookie not found in response"));
    }

    public static TcAuthService getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        private static final TcAuthService INSTANCE = new TcAuthService();
        private Holder(){
            throw new UnsupportedOperationException("TcAuthService.Holder should not be instantiated");
        }
    }
}