package tms.nicerglobe;

import tms.Consumer;
import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.util.List;

public class NicerGlobeConsumer implements Consumer {
    @Override
    public void updateEvents(List<Event> events) {

    }

    @Override
    public void updatePositions(List<Position> positions) {

    }

    @Override
    public void updateDevices(List<Device> devices) {

    }

    public static NicerGlobeConsumer getInstance(){
        return new NicerGlobeConsumer();
    }
}
