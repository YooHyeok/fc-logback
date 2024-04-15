package dev.be.logback.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MdcController {

    /**
     * mdc: 멀티쓰레드 환경에서 로그를 남길 때 사용하는 개념
     * Map과 동일한 자료구조로 key:value로 매핑해준다.
     * 스레드별로 MDC에 들어있는 값을 관리해준다.
     * 현재 스레드에서 설정한 key value값을 클리어하지 않고
     * 동일한 스레드를 참조하는 다른 요청에서 get("job")을 할 경우
     * "dev"값을 return받는다.
     * 따라서 MDC사용시 반드시 put과 clear를 pair단위로 사용하는것이 바람직하다.
     */
    @GetMapping("/mdc")
    public String mdc() {
        MDC.put("job", "dev");
        log.trace("log --> TRACE");
        log.debug("log --> DEBUG");
        log.info("log --> INFO");
        log.warn("log --> WARN");
        log.error("log --> ERROR");
        MDC.clear();
        return "mdc";
    }

}
