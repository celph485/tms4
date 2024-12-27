package tms.nicerglobe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.With;
import org.apache.commons.collections4.MapUtils;
import tms.common.UpperCasePropertyNamingStrategy;
import tms.tc.model.Position;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@With
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCasePropertyNamingStrategy.class)
public record Payload(
        Map<String, DataElement> dataElements,
        String vehicleNo,
        String gpsProviderKey
) {

    public static Payload getInstance(
            final Position position,
            final String vehicleRegistration,
            final String gpsProviderKey
    ){
        final String latitude = String.valueOf(position.latitude());
        final String longitude = String.valueOf(position.longitude());
        final String speed = String.valueOf(position.speed());
        final String heading = String.valueOf(position.course());
        final String datetime = convertToGmtDateTimeString(position.deviceTime());
        final String ignStatus = MapUtils.getBoolean(position.attributes(), "ignition", Boolean.FALSE) ? "1":"0";
        final String location = "";

        var dataElement = DataElement.builder()
                .latitude(latitude)
                .longitude(longitude)
                .speed(speed)
                .heading(heading)
                .datetime(datetime)
                .ignStatus(ignStatus)
                .location(location)
                .build();

        final var map = new HashMap<String, DataElement>();
        map.put("DATAELEMENTS", dataElement);

        return Payload.builder()
                .dataElements(map)
                .gpsProviderKey(gpsProviderKey)
                .vehicleNo(vehicleRegistration)
                .build();
    }

    private static String convertToGmtDateTimeString(ZonedDateTime deviceTime) {
        final var gmt = ZoneId.of("Etc/GMT");
        final var updated = deviceTime.withZoneSameInstant(gmt);
        return DateTimeFormatter
                .ofPattern("yyyy/MM/dd HH:mm:ss")
                .format(updated);
    }
}


@With
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCasePropertyNamingStrategy.class)
record DataElement(
        String latitude,
        String longitude,
        String speed,
        String heading,
        String datetime,
        String ignStatus,
        String location
) {
}