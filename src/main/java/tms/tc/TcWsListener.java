package tms.tc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.PropertiesLoader;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static tms.common.ConfigKey.TC_WS_READ_DELAY_SECONDS;

public final class TcWsListener implements WebSocket.Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcWsListener.class);

    private static final boolean ENABLE_DELAY = Boolean.FALSE;

    private static final long DELAY_SECONDS =
            Long.parseLong(PropertiesLoader.getInstance().getProperty(TC_WS_READ_DELAY_SECONDS));

    private final StringBuilder messageBuffer = new StringBuilder();

    private final TcWsMessageHandler tcWsMessageHandler;

    private TcWsListener(final TcWsMessageHandler tcWsMessageHandler){
        this.tcWsMessageHandler = tcWsMessageHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOGGER.info("WebSocket connection opened.");
        webSocket.request(1); // Request the first message
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        messageBuffer.append(data);
        if (last) {
            String fullMessage = messageBuffer.toString();
            messageBuffer.setLength(0); // Clear the buffer after use
            LOGGER.debug("Received WebSocket message: {}",fullMessage);
            tcWsMessageHandler.handleJsonMessage(fullMessage);
            if(ENABLE_DELAY){
                try {
                    TimeUnit.SECONDS.sleep(DELAY_SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        webSocket.request(1); // Request the next message
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        LOGGER.error("WebSocket error", error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        LOGGER.info("WebSocket closed. Status: {}, Reason: {}", statusCode, reason);
        return null;
    }

    public static TcWsListener getInstance(final TcWsMessageHandler tcWsMessageHandler){
        return new TcWsListener(tcWsMessageHandler);
    }
}