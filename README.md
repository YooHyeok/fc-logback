# Logback 개념

Sl4J(Simple Logging Facde for Java)라는 인터페이스를 구현하는 구현체이다.
즉, Logging Framework 라고 생각하면 된다.

## Appender 종류

- `ConsoleAppender`: 콘솔에 log를 출력
- `FileAppender`: 파일 단위로 log를 저장
- `RollingFileAppender`: (설정 옵션에 따라) log를 여러 파일로 나누어 저장
- `SMTPAppender`: log를 메일로 전송 하여 기록
- `DBAppender`: log를 DB에 저장

### `ConsoleAppender`
로컬 환경에서 개발할 때는 콘솔에 로그를 출력하고 바로바로 보는것이 좋을것이다.
특정 버그를 트래킹 한다던지 데이터들이 바인딩 되었는지 등 정상적으로 동작하는지를 확인하기 위해

### `FileAppender`
파일단위로 로그를 저장한다.
즉, 콘솔에 로그를 출력하는것이 아니라 파일에 로그를 출력하여 영구적으로 관리할 수 있게 된다.

### `RollingFileAppender`
하나의 파일에 모든 로그를 다 넣는것이 아니라 해당 로그를 여러파일로 쪼개서 저장하게 된다.
하나의 파일이 너무 커지는것을 방지하거나, 설정 옵션에 따라 정해진 기간내 파일을 유지,
정해진 기간에 자동으로 삭제 혹은 하루가 지나면 로그가 저장된 파일의 명칭을 어제의 날짜가 박힌 파일명으로 수정
등의 설정까지 가능하게 된다.
실제로 운영 서버에 jar파일을 배포할 때 운영서버에서는 IntellJ나 Eclipse같은 개발환경으로
서버를 띄우지 않기 때문에 Console로 로그를 출력을 할 수는 없을것이다.
따라서 운영 서버에서는 무조건(99%) 파일 어펜더를 사용한다.

### `SMTPAppender`
로그를 메일로 전송한다.

### `DBAppender`
로그를 DataBase에 저장한다.


## Logback 사용 궁극적 이유
운영을 위해서 Log는 반드시 필요하다.
Logback 설정을 하여 운영을 하기 위한 Log를 관리한다.
예를들어 특정 고객의 컴플레인이 들어왔을 때 Log를 확인하여 이슈를 분석하고
고객에게 해당 이슈에 대해 설명함으로써 대응할 수 있을것이다.
뿐만 아니라 데이터는 돈이므로 Log는 곧 값비싼 자산이다.
다양한 사용자들의 Log를 쌓음으로써 회원들의 패턴, 적절한곳에 어떤 기능을 추가하는게
사용자들에게 가장 큰 기대효과를 불러 일으킬 수 있는지 등을 데이터화 시켜야된다.
해당 데이터가 많은 부분이 Log이다.

# Lobback 설정 (Root)
- logback-spring.xml
  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <configuration>
      <include resource="org/springframework/boot/logging/logback/defaults.xml" />
  </configuration>
  ```

### Profile별 분기
로컬 환경에서는 IDE를 통해 Console에 log를 출력시켜 주기 때문에 거의 ConsoleAppender를 사용하게 된다.    
하지만 실제 운영환경의 경우에는 ConsoleAppender로 로그를 출력해도 직접 볼 수 가 없다.  
따라서 로그를 파일로 다로 저장할 수 있는 FileAppender를 사용한다.  
프로파일에 따라 파일별로 관리하도록 로그백 설정을 따로 가져가야 한다.  
- `logback-spring.xml  `
    ```xml
    <include resource="logback-spring-${spring.profile.active}.xml"/>
    ```
    위와같이 include를 설정할 경우 logback-spring-`local`.xml 혹은 logback-spring-`dev`.xml 와 같이 
    설정한 Profile값으로 지정되어 해당 파일을 로드한다.  

		
- `logback-spring-local.xml`
    ```xml
    <included>
        <include resource="org/springframework/boot/logging/logback/console-appender.xml">
    </included>
    ```
    Spring에서 지원하는 console-appender.xml 파일을 import한다.  
    해당 파일에는 아래와 같이 default한 설정이 들어있다.  

- `console-appender.xml`  
    해당 파일은 출력될 로그의 패턴과 charset이 설정되어있다.  
    해당 태그에 표현식 문법에 바인딩되는 값들은 앞서 include한 defaults.xml에 property로 설정되어있다.
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <included>
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>${CONSOLE_LOG_PATTERN}</pattern>
                <charset>${CONSOLE_LOG_CHARSET}</charset>
            </encoder>
        </appender>
    </included>
    ```
    즉, loback-spring.local.xml에서 include한다면 included태그안에 있는 appender의 내용이 import된다.

### root logging level 설정
	위와 같이 설정 후 실행해도 콘솔에 아무런 로그도 출력되지 않는다.
- logback-spring-local.xml
    ```xml
    <!-- 생략 -->
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
    ```
    위 코드를 추가한다.  
    root의 로깅 레벨을 DEBUG로 설정하였고, CONSOLE이라는 appender를 참조한다.  
    여기서 말하는 CONSOLE은 console-appender.xml에 정의된 appender의 name이다.  

### ConsoleAppender Custom
- logback-spring-local.xml
    ```xml
    <appender name="CUSTOM" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <layout>
            <pattern>
					[CUSTOM] [%-5level] %d{yyyy-MM-dd HH:mm:ss} [%thread] [%logger{0}:%line] - %msg%n
            </pattern>
        </layout>
    </appender>
    <root level="DEBUG">
        <appender-ref ref="CUSTOM"/>
    <root>
    ```
    - `filter` 와 `level`  
        ThresholdFilter 즉 Thread기반으로 필터링한다.  
        해당 filter를 통해 로깅 레벨을 INFO로 설정한다.  
        우리는 앞서 root의 로깅레벨을 DEBUG로 설정했다.  
        일반적으로 로깅레벨의 순위는 `TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR` 이다.  
        레벨이 낮을수록 훨씬 많은 로그를 상세하게 출력해준다.  
        즉, root의 로깅레벨인 DEBUG보다 INFO가 더 높으므로 CUSTOM관련 로그는 모두 INFO레벨로만 출력된다.  
        만약 CUSTOM과 CONSOLE 모두 켜져있다면 두 로그는 각각 다른 로깅레벨이 적용되어 동시에 출력된다.
<br/><br/>
    - `layout` & `pattern`  
        출력하고자 하는 패턴을 지정한다.  
        `[CUSTOM]`, `level을 5칸으로 표현`, `날짜및시간`, `실행된 스레드`, `logger의 출력할 라인값`, `메시지 밑 줄개행`



# *Properties 참조*

- `logback-variables.properties`
    ```yaml
    LOG_DIR=logs
    LOG_PATTERN=[%-5level] %d{yyyy-MM-dd HH:mm:ss} [%thread] [%logger{40}:%line] - %msg%n
    ```
- `logback-spring-prod.xml`
    ```xml
    <property resource="logback-variables.properties"/>
    <임시태그1>${LOG_DIR}/~~~~</임시태그1>
    <임시태그2>${LOG_PATTERN}</임시태그2>
    ```
위와같이 표현식으로 참조가 가능해진다.

# *MDC*
멀티스레드 환경에서 로그를 남길 때 사용하는 개념으로 컨트롤러나 파일 단위에 있어 완전 전역변수,  
쉽게말해 멀티스레드 환경에서 스레드마다 고유한 로그값을 갖고 있으며 Logback에 전달해 주기 위해서  
사용하는 개념이다.
MDC를 사용하는 대부분의 현업에서 사용 목적은 로그에서 해당 값에 저장되어 있는 값을  
동적으로 가져와 출력시키기 위해 사용한다.

```java
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
```
Map과 동일한 자료구조로 key:value로 매핑하여 사용한다.  
스레드별로 MDC에 들어있는 값을 관리해준다.  
현재 스레드에서 설정한 key value값을 clear하지 않고
동일한 스레드를 참조하는 다른 요청에서 get("job")을 할 경우 "dev값을 리턴한다.  
따라서 MDC 사용시 반드시 put과 clear를 pair단위로 사용하는것이 바람직하다.

- MDC 출력 appender 추가
    ```xml
    <appender name="MDC" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>${LOG_DIR}/mdc.log</file><!--파일명(경로) 지정-->
            <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                <fileNamePattern>${LOG_DIR}/archive/mdc.%d{yyyy-MM-dd}_%i.log</fileNamePattern><!-- 지정한 시간이 지나면 archive디렉토리 하위에 request1.날짜_순서.log 이름으로 저장-->
                <maxFileSize>10KB</maxFileSize> <!-- 최대file 사이즈 -->
                <maxHistory>30</maxHistory> <!-- 로그 파일 관리 최대 보관 주기 (단위: 일) -->
            </rollingPolicy>
            <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                <pattern>
                    [MDC] %X{job}%n <!-- %X를 사용하게 되면 MDC 안에 지정한 값을 Key로 조회할 수 있게 된다. -->
                </pattern>
                <outputPatternAsHeader>true</outputPatternAsHeader> <!-- 파일 헤더에 현재 어떠한 패턴으로 로그를 출력하는지를 함께 노출(저장) 시켜주는 설정 -->
            </encoder>
        </appender>
    ```

    ```xml
    <pattern>
        [MDC] %X{job}%n
    </pattern>
    ```
    주의깊게 봐야할 부분이 바로 위 코드이다.  
    %X를 사용하게 되면 MDC 안에 지정한 값을 Key로 조회할 수 있게 된다.

# *Filter (level)*
지정한 Level과 완벽하게 일치하는 Level의 Log만 출력되게 설정한다.

```xml
<filter class="ch.qos.logback.classic.filter.LevelFilter"><!-- Filter 옵션 추가 - 레벨 필터를 통해 레벨 단위로 필터링-->
    <level>warn</level>
    <onMatch>ACCEPT</onMatch> <!--완전 일치하다면 로그 허용-->
    <onMismatch>DENY</onMismatch> <!-- 완전일치하지 않으면 로그 거절 -->
</filter>
```
LevelFilter 클래스를 통해 Level단위로 필터링을 하게 된다.  
level을 지정한 뒤 onMatch 태그를 통해 완전 일치하다면 로그를 허용하도록 설정하고  
onMismatch 태그를 통해 일치하지 않으면 로그를 거절하도록 설정한다.  

# *logger (Root격리)*
Root로 부터 완전히 격리된 커스텀한 로거를 생성하여 사용한다.  
logger라는 이름의 태그를 통해 appender를 참조한다.  
- logback-spring-prod.xml
    ```xml
        <root level="INFO">
            <appender-ref ref="QUERY" />
        </root>
        <logger name="SQL_LOG1" level="INFO" additivity="false">
            <appender-ref ref="QUERY"/>
        </logger>
        <logger name="SQL_LOG2" level="INFO" additivity="false">
        <appender-ref ref="QUERY"/>
        </logger>
    ```
    additivity속성의 기본값은 true이며, 해당 속성을 통해 상위 로그에 대한 상속 여부를 결정한다.  
    즉, 기본값일 경우 Root에 동일한 appender가 등록되어있다면 해당 로그를 merge하여 함께 출력한다.  
    일반적으로 false로 설정하여 logger로 등록한 로그만 출력되게끔 사용한다.  


- ### `slf4j - topic 방식`  
   slf4j 애노테이션의 topic속성과 일치하는 logger 태그의 name속성을 참조하여 해당 appender에 설정된 로그를 적용한다.  
    ```java
    @Slf4j(topic = "SQL_LOG1") // 
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
    ```

- ### `LoggerFactory 방식`
    ```java
    @RestController
    public class QueryController2 {
        public static final Logger log = LoggerFactory.getLogger("SQL_LOG2");
        @GetMapping("/query2")
        public String query2() {
            log.trace("log --> TRACE");
            log.debug("log --> DEBUG");
            log.info("log --> INFO");
            log.warn("log --> WARN");
            log.error("log --> ERROR");
            return "query2";
        }
    }
    ```