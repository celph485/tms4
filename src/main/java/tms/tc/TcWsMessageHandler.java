package tms.tc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.DataStore;
import tms.common.JsonMapper;
import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.util.ArrayList;
import java.util.List;

public final class TcWsMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcWsMessageHandler.class);
    private final ObjectMapper objectMapper;
    private final DataStore dataStore;

    private TcWsMessageHandler(final JsonMapper jsonMapper, final DataStore dataStore){
        this.objectMapper = jsonMapper.objectMapper();
        this.dataStore = dataStore;
    }

    public void handleJsonMessage(final String jsonMessage) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonMessage);
            // Parse devices
            if (rootNode.has("devices")) {
                handleDevices(rootNode.get("devices"));
            }

            // Parse positions
            if (rootNode.has("positions")) {
                handlePositions(rootNode.get("positions"));
            }

            // Parse events
            if (rootNode.has("events")) {
                handleEvents(rootNode.get("events"));
            }

        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to process WebSocket message: {}", jsonMessage, e);
        }
    }

    public static TcWsMessageHandler getInstance(final JsonMapper jsonMapper, final DataStore dataStore){
        return new TcWsMessageHandler(jsonMapper, dataStore);
    }

    private void handleDevices(final JsonNode deviceNodes){
        List<Device> devices = new ArrayList<>();
        for (JsonNode deviceNode : deviceNodes) {
            try {
                devices.add(objectMapper.treeToValue(deviceNode, Device.class));
            } catch (Exception e) {
                LOGGER.error("Error parsing device: {}", deviceNode, e);
            }
        }
        LOGGER.debug("Devices: {}", devices);
        dataStore.devices(devices);
    }

    private void handlePositions(final JsonNode positionNodes){
        List<Position> positions = new ArrayList<>();
        for (JsonNode deviceNode : positionNodes) {
            try {
                positions.add(objectMapper.treeToValue(deviceNode, Position.class));
            } catch (Exception e) {
                LOGGER.error("Error parsing position: {}", deviceNode, e);
            }
        }
        LOGGER.debug("Positions: {}", positions);
        dataStore.positions(positions);
    }

    private void handleEvents(final JsonNode eventNodes){
        List<Event> events = new ArrayList<>();
        for (JsonNode deviceNode : eventNodes) {
            try {
                events.add(objectMapper.treeToValue(deviceNode, Event.class));
            } catch (Exception e) {
                LOGGER.error("Error parsing event: {}", deviceNode, e);
            }
        }
        LOGGER.debug("events: {}", events);
        dataStore.events(events);
    }
}
