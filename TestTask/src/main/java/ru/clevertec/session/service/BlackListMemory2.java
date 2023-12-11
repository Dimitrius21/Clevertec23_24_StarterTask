package ru.clevertec.session.service;

import org.springframework.stereotype.Component;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;

import java.util.List;

@Component
public class BlackListMemory2 implements BlackListSupplier {
    public List<String> getBannedList(){
        return List.of("Ivan", "Serge");
    }
}
