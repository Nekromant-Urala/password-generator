package ru.matthew.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class AppInfoConfig {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String version;
    @Value("${app.time.formatter}")
    private String DATA_FORMATTER;
    @Value("${app.message.name}")
    private String APP_NAME_MSG;
    @Value("${app.message.version}")
    private String VERSION_MSG;

    @PostConstruct
    public void init() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATA_FORMATTER);
        String formattedDate = now.format(formatter);
        System.out.printf(APP_NAME_MSG, formattedDate, appName);
        System.out.printf(VERSION_MSG, formattedDate, version);
    }
}
