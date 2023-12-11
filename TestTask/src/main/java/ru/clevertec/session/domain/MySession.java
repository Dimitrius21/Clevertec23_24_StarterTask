package ru.clevertec.session.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.clevertec.sessioninject.domain.StarterSession;

@AllArgsConstructor
@NoArgsConstructor
public class MySession extends StarterSession {
    private String email = "";

    @Override
    public String toString() {
        return super.toString() + ", email= " + email;
    }
}
