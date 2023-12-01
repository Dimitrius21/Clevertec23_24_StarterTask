package ru.clevertec.session.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.session.domain.Session;
import ru.clevertec.session.exception.NotFoundException;
import ru.clevertec.session.repository.SessionOperation;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class SessionService {
    SessionOperation repo;

    public Session create(String login) {
        return repo.create(login);
    }

    public Session getOrCreate(String login) {
        return repo.findByLogin(login)
                .filter(session -> {
                    if (session.getCreateTime().toLocalDate().isEqual(LocalDate.now())) return true;
                    else {
                        repo.delete(login);
                        return false;
                    }
                })
                .orElseGet(() -> repo.create(login));
    }

    public Session get(String login) {
        Session session = repo.findByLogin(login).orElseThrow(() -> new NotFoundException("Session for user with login= '" + login + "' is absent"));
        if (!session.getCreateTime().toLocalDate().isEqual(LocalDate.now())) {
            repo.delete(login);
            throw new NotFoundException("Session for user with login '" + login + "' is absent");
        }
        return session;
    }
}
