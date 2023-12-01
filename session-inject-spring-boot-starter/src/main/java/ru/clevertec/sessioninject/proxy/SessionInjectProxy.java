package ru.clevertec.sessioninject.proxy;

import lombok.AllArgsConstructor;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import ru.clevertec.sessioninject.StarterSession;
import ru.clevertec.sessioninject.exception.BannedException;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.LoginSupplier;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;
import ru.clevertec.sessioninject.util.BlackListForMethod;

import java.lang.reflect.Method;
import java.util.*;

@AllArgsConstructor
public class SessionInjectProxy implements MethodInterceptor {
    private SessionSupplier sessionSupplier;
    private List<BlackListForMethod> annotatedMethods;
    private BlackListSupplier blackListSupplier;
    private Map<Class, BlackListSupplier> blackListSupplierInAnnotation;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object res;
        Optional<BlackListForMethod> blackListForMethod = annotatedMethods.stream().filter(it -> it.method().equals(method)).findFirst();
        if (blackListForMethod.isPresent()) {
            List<Class> params = Arrays.asList(method.getParameterTypes());
            int loginPosition = params.indexOf(LoginSupplier.class);
            LoginSupplier loginSupplier = (LoginSupplier) args[loginPosition];
            String login = loginSupplier.getLogin();

            Set<String> banned = new HashSet<>();
            banned.addAll(blackListSupplier.getBannedList());
            List<Class> blackListClasses = blackListForMethod.get().classes();
            if (blackListClasses != null && !blackListClasses.isEmpty()) {
                List<String> bannedListInSuppliers = blackListClasses.stream()
                        .map(it -> blackListSupplierInAnnotation.get(it)
                                .getBannedList())
                        .flatMap(it -> it.stream())
                        .toList();
                banned.addAll(bannedListInSuppliers);
            }
            if (banned.contains(login)) {
                throw new BannedException("Access for you is forbidden");
            }

            int sessionPosition = params.indexOf(StarterSession.class);
            args[sessionPosition] = sessionSupplier.getSession(login);
            res = proxy.invokeSuper(obj, args);
            return res;
        } else {
            res = proxy.invokeSuper(obj, args);
        }
        return res;
    }
}
