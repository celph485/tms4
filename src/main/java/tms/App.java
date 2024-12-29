package tms;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import tms.common.*;
import tms.fretron.FreTronConsumer;
import tms.nicerglobe.NicerGlobeConsumer;
import tms.tc.TcAuthService;
import tms.tc.TcWsClient;
import tms.tc.TcWsListener;
import tms.tc.TcWsMessageHandler;

import java.io.IOException;
import java.net.URI;

import static tms.common.ConfigKey.*;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        CliArgProcessor.getInstance().process(args);

        final var props = PropertiesLoader.getInstance();
        final var dataStore = DataStore.getInstance();

        final var log = LoggerFactory.getLogger(App.class);

        if(StringUtils.equalsIgnoreCase("TRUE",props.getProperty(NICER_GLOBE_ENABLE))){
            log.info("Nicer globe consumer is enabled");
            final var nicerGlobeConsumer = NicerGlobeConsumer.builder()
                    .whiteListedDeviceIds(props.getListOfLongValues(NICER_GLOBE_ALLOWED_DEVICES))
                    .uri(URI.create(props.getProperty(NICER_GLOBE_API_ENDPOINT)))
                    .secret(props.getProperty(NICER_GLOBE_API_SECRET_KEY))
                    .build();
            dataStore.addConsumer(nicerGlobeConsumer);
        }

        if(StringUtils.equalsIgnoreCase("TRUE",props.getProperty(FT_ENABLE))){
            log.info("FreTron consumer is enabled");
            final var freTronConsumer = FreTronConsumer.builder()
                    .whiteListedDeviceIds(props.getListOfLongValues(FT_ALLOWED_DEVICES))
                    .uri(URI.create(props.getProperty(FT_API_ENDPOINT)))
                    .secret(props.getProperty(FT_API_VENDOR))
                    .build();
            dataStore.addConsumer(freTronConsumer);
        }

        if(dataStore.hasAnyConsumers()){
            final var wsMessageHandler = TcWsMessageHandler.getInstance(
                    JsonMapper.getInstance(),
                    dataStore
            );

            final var wsListener = TcWsListener.getInstance(wsMessageHandler);

            final var wsClient = TcWsClient.getInstance(wsListener);

            final var sessionCookie = TcAuthService.getInstance().authenticate();

            wsClient.connect(sessionCookie);
        }else {
            log.warn("No consumer is enabled, so stopping...");
        }
    }
}
