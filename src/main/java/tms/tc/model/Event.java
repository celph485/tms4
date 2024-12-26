package tms.tc.model;

import lombok.Builder;
import lombok.With;

import java.util.Map;

@With
@Builder
public record Event(
        long id,
        long deviceId,
        String type,
        Map<String, Object> attributes,
        long timestamp
) implements WebSocketMessage {
}
