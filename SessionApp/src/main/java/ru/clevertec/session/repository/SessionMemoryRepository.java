package ru.clevertec.session.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.session.domain.Session;
import ru.clevertec.session.domain.Sessions;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SessionMemoryRepository implements SessionOperation {
    public Sessions sessions;

    @Override
    public Optional<Session> findByLogin(String login) {
        return sessions.getSessions(login);
    }

    @Override
    public Session create(String login) {
        return sessions.create(login);
    }

    @Override
    public void delete(String login) {
        sessions.delete(login);
    }
}
