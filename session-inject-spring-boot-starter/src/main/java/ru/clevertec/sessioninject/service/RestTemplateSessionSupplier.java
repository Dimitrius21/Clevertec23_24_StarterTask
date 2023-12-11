package ru.clevertec.sessioninject.service;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.sessioninject.domain.StarterSession;
import ru.clevertec.sessioninject.exception.SessionNotAvailableException;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;

import java.time.Duration;

@Service
@Setter
@NoArgsConstructor
public class RestTemplateSessionSupplier implements SessionSupplier {
    @Value("${session.inject.url}")
    private String url;

    @Override
    public StarterSession getSession(String login) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(500))
                .setReadTimeout(Duration.ofMillis(500))
                .build();
        try {
            ResponseEntity<StarterSession> answer = restTemplate.getForEntity(url + login, StarterSession.class);
            if (answer.getStatusCode() != HttpStatusCode.valueOf(200)) {
                throw new SessionNotAvailableException("Server with Session is not available");
            }
            StarterSession session = answer.getBody();
            return session;
        } catch (RestClientException e) {
            throw new SessionNotAvailableException("Server with Session is not available");
        }
    }
}
