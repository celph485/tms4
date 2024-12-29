package tms.common;

import tms.Consumer;
import tms.tc.model.Device;
import tms.tc.model.Event;
import tms.tc.model.Position;

import java.util.ArrayList;
import java.util.List;

public final class DataStore {

    private final List<Consumer> consumers;

    private DataStore(){
        this.consumers = new ArrayList<>();
    }

    public void devices(final List<Device> devices) {
        consumers.forEach(c -> c.updateDevices(List.copyOf(devices)));
    }

    public void events(List<Event> events) {
        consumers.forEach(c -> c.updateEvents(List.copyOf(events)));
    }

    public void positions(List<Position> positions) {
        consumers.parallelStream().forEach(c -> c.updatePositions(List.copyOf(positions)));
    }

    public void addConsumer(final Consumer consumer){
        this.consumers.add(consumer);
    }

    public boolean hasAnyConsumers(){
        return !this.consumers.isEmpty();
    }

    public static DataStore getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        private static final DataStore INSTANCE = new DataStore();
        private Holder(){
            throw new UnsupportedOperationException("DataStore.Holder should not be instantiated");
        }
    }
}
