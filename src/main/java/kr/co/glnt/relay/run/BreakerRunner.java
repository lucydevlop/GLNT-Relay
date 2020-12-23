package kr.co.glnt.relay.run;

import kr.co.glnt.relay.breaker.web.GpmsAPI;
import kr.co.glnt.relay.breaker.web.NgisAPI;
import kr.co.glnt.relay.common.config.ServerConfig;
import kr.co.glnt.relay.breaker.dto.FacilityInfo;
import kr.co.glnt.relay.breaker.dto.FacilityInfoPayload;
import kr.co.glnt.relay.breaker.watcher.GlntFolderWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 애플리케션 실행시 동작하는 Runner 클래스
 */
@Slf4j
@Component
public class BreakerRunner implements ApplicationRunner {
    private final ServerConfig config;
    private final GpmsAPI gpmsAPI;
    private final NgisAPI ngisAPI;
    public static Queue<String> entranceQueue = new LinkedList<>();

    public BreakerRunner(ServerConfig config, GpmsAPI gpmsAPI, NgisAPI ngisAPI) {
        this.config = config;
        this.gpmsAPI = gpmsAPI;
        this.ngisAPI = ngisAPI;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. get parking lot info data
        List<FacilityInfo> facilityList = gpmsAPI.getParkinglotData(new FacilityInfoPayload(config.getServerName()));
        if (Objects.isNull(facilityList) || facilityList.size() == 0) {
            log.error("주차장 게이트정보 조회를 실패하여 프로그램을 종료합니다.");
            System.exit(0);
        }
        config.setFacilityList(facilityList);

        int isOpen = ngisAPI.requestNgisOpen();
        if (isOpen < 0) {
            log.error("인식 모듈 연결이 실패했습니다.");
            System.exit(0);
        }

        // 2. data grouping (in gate / out gate)
        Map<String, List<FacilityInfo>> parkingGroup = facilityList.stream()
                .filter(info -> Objects.nonNull(info.getImagePath()))
                .collect(Collectors.groupingBy(FacilityInfo::getImagePath));


        // 2. watcher thread 실행
        parkingGroup.forEach((key, value) -> {
            GlntFolderWatcher watcher = new GlntFolderWatcher(value.get(0));
            Thread watcherThread = new Thread(watcher);
            watcherThread.setName(value.get(0).getGateLprType());
            watcherThread.start();
        });

//        new Thread(new EntranceQueue(ngisAPI)).start();


//        for (;;) {
//            for (int i = 0; i < 5; i++) {
//                File file = new File("C:\\park\\"+ i +".jpg");
//                FileUtils.moveFile(file, new File("C:\\park\\in_front\\" +i+ ".jpg"));
//            }
//
//            Thread.sleep(2000);
//
//            for (int i = 0; i < 5; i++) {
//                File file = new File("C:\\park\\"+ i +".jpg");
//                FileUtils.moveFile(new File("C:\\park\\in_front\\" +i+ ".jpg"), file);
//            }
//
//            Thread.sleep(2000);
//
//        }



    }

}