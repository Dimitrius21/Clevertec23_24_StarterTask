package ru.clevertec.session.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private String id;
    private String login;
    private LocalDateTime createTime;

    public static SessionDto toSessionDto(Session session) {
        return new SessionDto(Long.toString(session.getId()), session.getLogin(), session.getCreateTime());
    }
}
