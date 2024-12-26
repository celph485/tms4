package tms;

import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.util.List;

public interface Consumer {
    void updateEvents(final List<Event> events);
    void updatePositions(final List<Position> positions);
    void updateDevices(final List<Device> devices);
}
