### Logstash

- `LoggingEventAsyncDisruptorAppender` provides much better multithreading performance than the default `AsyncAppender` in logback.
```xml
<configuration>
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>./logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>./logs-archive/app.%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <charset>UTF-8</charset>
            <pattern>%d{HH:mm:ss.SSS} [%level] [%class{0}] - %m%n</pattern>
        </encoder>
    </appender>
    <appender name="async-file" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="file"/>
    </appender>
</configuration>
```
