package ru.clevertec.sessioninject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarterSession {
    private long id;
    private String login;
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return "id=" + id + ", login='" + login + "', create at " + createTime;
    }
}
