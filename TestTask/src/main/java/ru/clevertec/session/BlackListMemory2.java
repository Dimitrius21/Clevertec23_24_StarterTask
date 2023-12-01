package ru.clevertec.session;

import ru.clevertec.sessioninject.interfaces.BlackListSupplier;

import java.util.List;

public class BlackListMemory2 implements BlackListSupplier {
    public List<String> getBannedList(){
        return List.of("Ivan", "Serge");
    }
}
