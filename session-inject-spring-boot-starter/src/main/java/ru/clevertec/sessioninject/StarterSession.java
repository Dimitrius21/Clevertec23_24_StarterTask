package ru.clevertec.sessioninject;

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

}
