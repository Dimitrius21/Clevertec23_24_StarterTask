package ru.clevertec.sessioninject.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "session.inject")
public class SessionInjectProperties {
    private List<String> bannedList;
    private String url;
}
