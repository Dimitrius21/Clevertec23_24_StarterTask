package ru.clevertec.session.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.session.domain.SessionDto;
import ru.clevertec.session.service.SessionService;

@Controller
@RequestMapping("/session")
@AllArgsConstructor
public class SessionController {
    private SessionService service;

    @GetMapping
    public ResponseEntity<SessionDto> getSession(@RequestParam String login) {
        SessionDto dto = SessionDto.toSessionDto(service.get(login));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("/create")
    public ResponseEntity<SessionDto> getOrCreateSession(@RequestParam String login) {
        SessionDto dto = SessionDto.toSessionDto(service.getOrCreate(login));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(@RequestBody SessionDto dto) {
        dto = SessionDto.toSessionDto(service.create(dto.getLogin()));
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


}
