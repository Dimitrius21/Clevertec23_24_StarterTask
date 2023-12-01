package ru.clevertec.sessioninject.interfaces;


import ru.clevertec.sessioninject.StarterSession;

public interface SessionSupplier {
    public StarterSession getSession(String login);
}
