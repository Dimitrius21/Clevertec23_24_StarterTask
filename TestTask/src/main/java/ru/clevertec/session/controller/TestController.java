package ru.clevertec.session.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.session.domain.MySession;
import ru.clevertec.session.TestMethod;
import ru.clevertec.sessioninject.domain.StarterSession;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {
    private TestMethod test;

    @GetMapping
    public String getRequest(@RequestParam String login) {
        test.methodOne("one");
        String res1 = test.methodTwo("two", null);
        String res2 = test.methodThree("three", new StarterSession(), () -> login);
        String res3 = test.methodFour("four", new MySession(), () -> login);
        return res1 + ", " + res2 + ", " + res3;
    }
}
