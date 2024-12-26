package tms.tc.model;

import lombok.Builder;
import lombok.With;

import java.time.ZonedDateTime;
import java.util.Map;

@With
@Builder
public record Device(
        long id,
        String name,
        String uniqueId,
        String status,
        Long positionId,
        Map<String, Object> attributes,
        ZonedDateTime lastUpdate,
        long groupId,
        long calendarId,
        String phone,
        String model,
        String contact,
        String category,
        boolean disabled,
        ZonedDateTime expirationTime
) implements WebSocketMessage {
}
