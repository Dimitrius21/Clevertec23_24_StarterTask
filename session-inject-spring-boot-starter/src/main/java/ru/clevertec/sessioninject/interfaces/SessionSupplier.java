package ru.clevertec.sessioninject.interfaces;


import ru.clevertec.sessioninject.domain.StarterSession;

public interface SessionSupplier {
    StarterSession getSession(String login);
    void setUrl(String url);
}
