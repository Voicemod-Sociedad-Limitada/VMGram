package org.telegram.vm.tracking;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrackingEvent {

    public String eventType;
    public Map<String, Object> properties;

    public TrackingEvent(String eventType, Map<String, Object> properties) {
        this.eventType = eventType;
        this.properties = properties;
    }

    public TrackingEvent(String eventType) {
        this(eventType, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEvent that = (TrackingEvent) o;
        return Objects.equals(eventType, that.eventType) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, properties);
    }

    @Override
    public String toString() {
        return "TrackingEvent{" +
                "eventType='" + eventType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
