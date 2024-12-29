package tms.nicerglobe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.Consumer;
import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NicerGlobeConsumer implements Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NicerGlobeConsumer.class);

    private final List<Long> whiteListedDeviceIds;

    private final Map<Long, Device> deviceByIdMap;

    private final NicerGlobeClient client;

    private final String secrete;

    private NicerGlobeConsumer(
            List<Long> whiteListedDeviceIds,
            final URI uri,
            final String secrete
    ) {
        LOGGER.debug("Creating object of NicerGlobeConsumer");
        this.whiteListedDeviceIds = whiteListedDeviceIds;
        this.client = NicerGlobeClient.getInstance(uri);
        this.secrete = secrete;
        this.deviceByIdMap = new HashMap<>();
    }

    @Override
    public void updatePositions(List<Position> positions) {
        LOGGER.info("processing {} positions", positions.size());
        if (deviceByIdMap.isEmpty()){
            LOGGER.warn("DeviceByIdMap is empty, so can not process positions");
            return;
        }
        LOGGER.debug("whiteListedDeviceIds: {}", whiteListedDeviceIds);
        final var payloads = positions.stream()
                .filter(p -> whiteListedDeviceIds.contains(p.deviceId()))
                .map(this::toPayload)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        if(CollectionUtils.size(payloads) > 0){
            client.sendData(payloads);
        }
    }

    @Override
    public void updateDevices(List<Device> devices) {
        deviceByIdMap.putAll(updateDeviceByIdMap(
                Map.copyOf(deviceByIdMap),
                List.copyOf(whiteListedDeviceIds),
                devices
        ));
    }

    @Override
    public void updateEvents(List<Event> events) {
    }

    private Map<Long, Device> updateDeviceByIdMap(
            Map<Long, Device> deviceByIdMap,
            List<Long> whiteListedDeviceIds,
            List<Device> devices
    ) {
        LOGGER.debug("Total devices is deviceByIdMap: {}", MapUtils.size(deviceByIdMap));
        LOGGER.debug("Total whitelisted devices: {}", CollectionUtils.size(whiteListedDeviceIds));
        LOGGER.debug("Total {} device info came from server", CollectionUtils.size(devices));
        return devices
                .stream()
                .filter(d-> !deviceByIdMap.containsKey(d.id()))
                .filter( d -> whiteListedDeviceIds.contains(d.id()))
                .collect(Collectors.toMap(Device::id, Function.identity()));
    }

    private Optional<Payload> toPayload(final Position position){
        final var deviceId = position.deviceId();
        final var device = deviceByIdMap.get(deviceId);

        if(Objects.isNull(device)){
            return Optional.empty();
        }
        return Optional.of(Payload.getInstance(position, device.name(), secrete));
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private List<Long> whiteListedDeviceIds;
        private URI uri;
        private String secret;

        public Builder whiteListedDeviceIds(final List<Long> whiteListedDeviceIds) {
            this.whiteListedDeviceIds = List.copyOf(whiteListedDeviceIds);
            return this;
        }

        public Builder uri(final URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder secret(final String secret) {
            this.secret = secret;
            return this;
        }

        public NicerGlobeConsumer build(){
            return new NicerGlobeConsumer(
                    whiteListedDeviceIds,
                    uri,
                    secret
            );
        }
    }
}
