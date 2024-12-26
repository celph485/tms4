package tms.fretron;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.With;
import tms.tc.model.Position;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@With
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Payload(
        String imei,
        float latitude,
        float longitude,
        String vendor,
        long time,
        int speed
) {

    public static Payload getInstance(final Position position, final String imeiNo, final String vendor){
        return Payload
                .builder()
                .imei(imeiNo)
                .vendor(vendor)
                .latitude(position.latitude())
                .longitude(position.longitude())
                .time(convertToIstDateTimeEpoch(position.deviceTime()))
                .speed(Math.round(position.speed()))
                .build();
    }

    private static long convertToIstDateTimeEpoch(ZonedDateTime zonedDateTime) {
        return zonedDateTime
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .toInstant()
                .toEpochMilli();
    }
}