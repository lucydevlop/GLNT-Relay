package kr.co.glnt.relay.dto;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.TimeZone;

/**
 * 차량 입차 요청에 쓰이는 클래스
 */
@Data
public class ParkInOutPayload {
    private String uuid;
    private String vehicleNo;       // 인식된 차량번호 (Default: "", Success: 차량번호)
    private String dtFacilitiesId;    // 장치 아이디(어디 게이트에 어떤차단기)
    private String resultcode;      // OCR 판독 결과 코드

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;   // 차량 입차 시간
    private String base64Str;       // 사진파일 encode

    @SneakyThrows
    public ParkInOutPayload(String key, CarInfo carInfo) {
        uuid = key;
        vehicleNo = carInfo.getNumber();
        dtFacilitiesId = carInfo.getDtFacilitiesId();
        resultcode = String.valueOf(carInfo.getCode());
        date = LocalDateTime.ofInstant(Instant.ofEpochMilli(carInfo.getInDate()),
                TimeZone.getDefault().toZoneId());
        byte[] fileContent = FileUtils.readFileToByteArray(new File(carInfo.getFullPath()));
        base64Str = Base64.getEncoder().encodeToString(fileContent);
    }
}
