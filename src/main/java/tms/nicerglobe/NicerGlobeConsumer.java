package tms.nicerglobe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.common.AbstractConsumer;
import tms.common.PropertiesLoader;
import tms.tc.model.Position;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static tms.common.ConfigKey.*;

public class NicerGlobeConsumer extends AbstractConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NicerGlobeConsumer.class);

    private final List<Long> whiteListedDeviceIds;

    private final NicerGlobeClient client;

    private final String secrete;

    public NicerGlobeConsumer() {
        LOGGER.debug("Creating object of NicerGlobeConsumer");
        final var props = PropertiesLoader.getInstance();
        this.secrete = props.getProperty(NICER_GLOBE_API_SECRET_KEY);
        final var deviceIdList = props.getProperty(NICER_GLOBE_ALLOWED_DEVICES, StringUtils.EMPTY);
        this.whiteListedDeviceIds = Arrays.stream(deviceIdList.split(","))
                .map(Long::parseLong)
                .toList();
        final var uri = URI.create(props.getProperty(NICER_GLOBE_API_ENDPOINT));
        this.client = NicerGlobeClient.getInstance(uri);
    }

    @Override
    public void updatePositions(List<Position> positions) {
        LOGGER.info("processing {} positions", positions.size());
        if (deviceByIdMap().isEmpty()){
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
            payloads.forEach(client::sendData);
        }
    }

    private Optional<Payload> toPayload(final Position position){
        final var deviceId = position.deviceId();
        final var device = deviceByIdMap().get(deviceId);

        if(Objects.isNull(device)){
            return Optional.empty();
        }
        return Optional.of(Payload.getInstance(position, device.name(), secrete));
    }

    public static NicerGlobeConsumer getInstance(){
        return new NicerGlobeConsumer();
    }
}
