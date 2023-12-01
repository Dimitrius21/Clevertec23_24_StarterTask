package ru.clevertec.session.repository;

import ru.clevertec.session.domain.Session;

import java.util.Optional;

public interface SessionOperation {
    public Optional<Session> findByLogin(String login);

    public Session create(String login);

    public void delete(String login);
}
