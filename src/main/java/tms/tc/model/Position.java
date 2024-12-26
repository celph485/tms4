package tms.tc.model;

import lombok.Builder;
import lombok.With;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@With
@Builder
public record Position(
        long id,
        long deviceId,
        String protocol,
        ZonedDateTime serverTime,
        ZonedDateTime deviceTime,
        ZonedDateTime fixTime,
        boolean outdated,
        boolean valid,
        float latitude,
        float longitude,
        float altitude,
        float speed,
        float course,
        String address,
        int accuracy,
        Map<String, Object> network,
        List<Long> geofenceIds,
        Map<String, Object> attributes
) implements WebSocketMessage {
}
