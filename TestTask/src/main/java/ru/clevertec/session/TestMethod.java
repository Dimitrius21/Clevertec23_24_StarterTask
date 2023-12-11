package ru.clevertec.session;

import org.springframework.stereotype.Component;
import ru.clevertec.session.domain.MySession;
import ru.clevertec.session.service.BlackListMemory;
import ru.clevertec.session.service.BlackListMemory2;
import ru.clevertec.sessioninject.domain.StarterSession;
import ru.clevertec.sessioninject.interfaces.LoginSupplier;
import ru.clevertec.sessioninject.annotation.SessionInject;

@Component
public class TestMethod {
    @SessionInject
    public String methodOne(String str) {
        System.out.println("methodOne");
        return "methodOne";
    }

    @SessionInject
    public String methodTwo(String str, StarterSession session) {
        System.out.println("methodTwo");
        String result = "Session for methodTwo: " + session;
        System.out.println(result);
        return result;
    }

    @SessionInject(blackList = {BlackListMemory.class, BlackListMemory2.class})
    public String methodThree(String str, StarterSession session, LoginSupplier login) {
        System.out.println("methodThree");
        String result = "Session for methodThree: " + session;
        System.out.println(result);
        return result;
    }

    @SessionInject(blackList = {BlackListMemory.class, BlackListMemory2.class})
    public String methodFour(String str, MySession session, LoginSupplier login) {
        System.out.println("methodFour");
        String result = "Session for methodFour: " + session;
        System.out.println(result);
        return result;
    }


    @Override
    public String toString() {
        return "TestMethod{}";
    }
}
