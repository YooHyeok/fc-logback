package dev.be.logback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "SQL_LOG1") // topic과 일치하는 logger 태그의 name속성을 참조하여 해당 appender에 설정된 로그를 적용
@RestController
public class QueryController1 {
    @GetMapping("/query1")
    public String query1() {
        log.trace("log --> TRACE");
        log.debug("log --> DEBUG");
        log.info("log --> INFO");
        log.warn("log --> WARN");
        log.error("log --> ERROR");
        return "query1";
    }
}
