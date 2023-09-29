package ru.bereshs.HHWorkSearch.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    String hhApiUri;
    String hhUserAgent;
    String hhClientId;
    String hhClientSecret;
    String emailEmail;
    String emailPassword;
    String hhVacancy;
    String hhResume;
}
