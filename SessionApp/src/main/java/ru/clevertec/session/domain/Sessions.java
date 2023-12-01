package ru.clevertec.session.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Sessions {
    private Lock lock = new ReentrantLock();
    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private long id = 0;

    public Session create(String login) {
        try {
            lock.lock();
            Session session = sessions.get(login);
            if (Objects.isNull(session)) {
                session = new Session(++id, login, LocalDateTime.now());
                sessions.put(login, session);
            }
            return session;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Session> getSessions(String login) {
        Session session = sessions.get(login);
        return Optional.ofNullable(session);
    }

    public void delete(String login) {
        try {
            lock.lock();
            sessions.remove(login);
        } finally {
            lock.unlock();
        }
    }


}
