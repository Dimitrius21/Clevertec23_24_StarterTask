package ru.clevertec.sessioninject.proxy;

import lombok.AllArgsConstructor;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import ru.clevertec.sessioninject.domain.BlackListForMethod;
import ru.clevertec.sessioninject.domain.StarterSession;
import ru.clevertec.sessioninject.exception.BannedException;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.LoginSupplier;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@AllArgsConstructor
public class SessionInjectInterceptor implements MethodInterceptor {
    private SessionSupplier sessionSupplier;
    private List<BlackListForMethod> annotatedMethods;
    private BlackListSupplier blackListSupplierInProperty;
    private Map<Class, BlackListSupplier> blackListSupplierInAnnotation;
    private int[] position;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        Optional<BlackListForMethod> blackListForMethod = annotatedMethods.stream().filter(it -> it.method().equals(method)).findFirst();
        if (blackListForMethod.isPresent()) {
            String login = getLogin(args);
            Collection<String> bannedList = getBannedList(blackListForMethod.get());
            if (bannedList.contains(login)) {
                throw new BannedException("Access for you is forbidden");
            }
            Object sessionArg = args[position[0]];
            StarterSession session = sessionSupplier.getSession(login);
            if (Objects.isNull(sessionArg)) {
                args[position[0]] = session;
            } else {
                injectSessionDataToArgument(session, sessionArg);
            }
        }
        return proxy.invokeSuper(obj, args);
    }

    private String getLogin(Object[] args) {
        LoginSupplier loginSupplier = (LoginSupplier) args[position[1]];
        return loginSupplier.getLogin();
    }

    private Set<String> getBannedList(BlackListForMethod blackListForMethod) {
        Set<String> banned = new HashSet<>();
        banned.addAll(blackListSupplierInProperty.getBannedList());
        List<Class> blackListClasses = blackListForMethod.classes();
        if (blackListClasses != null && blackListClasses.size() > 0) {
            List<String> bannedListInSuppliers = blackListClasses.stream()
                    .map(it -> blackListSupplierInAnnotation.get(it)
                            .getBannedList())
                    .flatMap(it -> it.stream())
                    .toList();
            banned.addAll(bannedListInSuppliers);
        }
        return banned;
    }

    private void injectSessionDataToArgument(StarterSession session, Object sessionArg) throws NoSuchFieldException, IllegalAccessException {
        Class sessionClass = StarterSession.class;
        Field field = sessionClass.getDeclaredField("login");
        field.setAccessible(true);
        field.set(sessionArg, session.getLogin());
        field = sessionClass.getDeclaredField("id");
        field.setAccessible(true);
        field.set(sessionArg, session.getId());
        field = sessionClass.getDeclaredField("createTime");
        field.setAccessible(true);
        field.set(sessionArg, session.getCreateTime());
    }
}

