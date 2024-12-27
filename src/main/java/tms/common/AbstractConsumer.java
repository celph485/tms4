package tms.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tms.Consumer;
import tms.tc.model.Device;
import tms.tc.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractConsumer implements Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsumer.class);

    private final Map<Long, Device> deviceByIdMap;

    public AbstractConsumer(){
        this.deviceByIdMap = new HashMap<>();
    }

    public Map<Long, Device> deviceByIdMap() {
        return deviceByIdMap;
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

    @Override
    public void updateEvents(final List<Event> events) {

    }


}
