package tms;

import tms.common.CliArgProcessor;
import tms.common.DataStore;
import tms.common.JsonMapper;
import tms.fretron.FreTronConsumer;
import tms.nicerglobe.NicerGlobeConsumer;
import tms.tc.TcAuthService;
import tms.tc.TcWsClient;
import tms.tc.TcWsListener;
import tms.tc.TcWsMessageHandler;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        CliArgProcessor.getInstance().process(args);

        final var nicerGlobeConsumer = NicerGlobeConsumer.getInstance();
        final var freTronConsumer = FreTronConsumer.getInstance();

        final var dataStore = DataStore.getInstance();

        dataStore.addConsumer(freTronConsumer);
        dataStore.addConsumer(nicerGlobeConsumer);

        final var wsMessageHandler = TcWsMessageHandler.getInstance(
                JsonMapper.getInstance(),
                dataStore
        );

        final var wsListener = TcWsListener.getInstance(wsMessageHandler);

        final var wsClient = TcWsClient.getInstance(wsListener);

        final var sessionCookie = TcAuthService.getInstance().authenticate();

        wsClient.connect(sessionCookie);

    }
}
