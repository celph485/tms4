package tms.fretron;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.Consumer;
import tms.common.PropertiesLoader;
import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tms.common.ConfigKey.*;

public final class FreTronConsumer implements Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreTronConsumer.class);

    private final Map<Long, Device> deviceByIdMap;

    private final List<Long> whiteListedDeviceIds;

    private final FreTronClient client;
    private final String vendor;

    private FreTronConsumer(){
        final var props = PropertiesLoader.getInstance();
        this.vendor = props.getProperty(FT_API_VENDOR);
        deviceByIdMap = new HashMap<>();
        final var deviceIdList = props.getProperty(FT_ALLOWED_DEVICES, StringUtils.EMPTY);
        this.whiteListedDeviceIds = Arrays.stream(deviceIdList.split(","))
                .map(Long::parseLong)
                .toList();
        final var uri = URI.create(props.getProperty(FT_API_ENDPOINT));
        this.client = FreTronClient.getInstance(uri);
    }

    @Override
    public void updateEvents(final List<Event> events) {
    }

    @Override
    public void updatePositions(final List<Position> positions) {
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
    public void updateDevices(final List<Device> devices) {
        LOGGER.debug("Total devices: {}", CollectionUtils.size(devices));
        updateDeviceByIdMap(devices);
    }

    private void updateDeviceByIdMap(final List<Device> devices){
        LOGGER.debug("populating deviceByIdMap with {} devices", devices.size());
        final var map = devices.stream()
                .collect(Collectors.toMap(Device::id, Function.identity()));
        this.deviceByIdMap.putAll(map);
        LOGGER.debug("deviceByIdMap size: {}", MapUtils.size(this.deviceByIdMap));

    }

    private Optional<Payload> toPayload(final Position position){
        final var deviceId = position.deviceId();
        final var device = deviceByIdMap.get(deviceId);

        if(Objects.isNull(device)){
            return Optional.empty();
        }

        return Optional.of(Payload.getInstance(position, device.uniqueId(), vendor));
    }

    public static FreTronConsumer getInstance(){
        return new FreTronConsumer();
    }
}
