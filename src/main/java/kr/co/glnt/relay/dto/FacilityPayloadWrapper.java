package kr.co.glnt.relay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter @RequiredArgsConstructor
public class FacilityPayloadWrapper {
    private final Object facilitiesList;

    public static FacilityPayloadWrapper healthCheckPayload(List<FacilityStatus> payload) {
        return new FacilityPayloadWrapper(payload);
    }

    public static FacilityPayloadWrapper facilityAlarmPayload(List<FacilityStatus> payload) {
        return new FacilityPayloadWrapper(payload);
    }
}
