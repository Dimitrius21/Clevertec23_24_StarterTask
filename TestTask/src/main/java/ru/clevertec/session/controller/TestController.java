package ru.clevertec.session.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.session.TestMethod;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {
    private TestMethod test;

    @GetMapping
    public String getRequest(@RequestParam String login) {
        test.methodOne("one");
        String res1 = test.methodTwo("two", null);
        String res2 = test.methodThree("three", null, () -> login);
        return res1 + ", " + res2;
    }
}
