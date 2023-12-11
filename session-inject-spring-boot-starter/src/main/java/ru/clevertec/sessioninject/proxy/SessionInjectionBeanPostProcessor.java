package ru.clevertec.sessioninject.proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import ru.clevertec.sessioninject.domain.StarterSession;
import ru.clevertec.sessioninject.exception.CreateObjectException;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.LoginSupplier;
import ru.clevertec.sessioninject.annotation.SessionInject;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;
import ru.clevertec.sessioninject.domain.BlackListForMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class SessionInjectionBeanPostProcessor implements BeanPostProcessor {
    private Map<String, List<BlackListForMethod>> objectsHaveAnnotatedMethods = new HashMap<>();
    private final Map<Class, BlackListSupplier> blackListSupplierObjects;
    private final SessionSupplier sessionSupplier;
    private final BlackListSupplier bannedSupplierInProperty;
    private int[] position = new int[2];

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Method[] methods = bean.getClass().getDeclaredMethods();
        List<BlackListForMethod> methodForInjection = Arrays.stream(methods)
                .filter(this::isMethodAnnotatedCorrect)
                .map(this::getBlackListClasses)
                .toList();
        if (methodForInjection.size() > 0) {
            objectsHaveAnnotatedMethods.put(beanName, methodForInjection);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (objectsHaveAnnotatedMethods.get(beanName) != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new SessionInjectInterceptor(sessionSupplier, objectsHaveAnnotatedMethods.get(beanName),
                    bannedSupplierInProperty, blackListSupplierObjects, position));
            bean = enhancer.create();
        }
        return bean;
    }

    /**
     * Проверяет метод на предмет его соответствия требованиям для добавления логики получения Сессии
     * (анотация, в аргументах метода есть Сессия и LoginSupplier)
     *
     * @param method анализируемый метод
     * @return true/false в зависимости от соответствия
     */
    private boolean isMethodAnnotatedCorrect(Method method) {
        boolean hasSession = false;
        boolean hasLoginSupplier = false;
        if (method.isAnnotationPresent(SessionInject.class)) {
            Class[] parameters = method.getParameterTypes();
            for (int i = 0; i < parameters.length; i++) {
                if (isStarterSession(parameters[i])) {
                    hasSession = true;
                    position[0] = i;
                }
                if (hasLoginSupplierInterface(parameters[i])) {
                    hasLoginSupplier = true;
                    position[1] = i;
                }
                if (hasSession & hasLoginSupplier) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Создать record содержащий имя метода и список классов, поставляющих заблокированные логины, указанный в
     * анотации над методом
     *
     * @param method анализируемый метод
     * @return record с данными
     */

    private BlackListForMethod getBlackListClasses(Method method) {
        SessionInject annotation = method.getAnnotation(SessionInject.class);
        List<Class> blackListSupplierClasses = Arrays.asList(annotation.blackList());
        blackListSupplierClasses.forEach((it -> {
            if (blackListSupplierObjects.get(it) == null) {
                throw new CreateObjectException("No bean for " + it.getName());
            }
        }));
        return new BlackListForMethod(method, blackListSupplierClasses);
    }

    /**
     * Создает объект класса, являющего поставщиком списка заблокированных логинов,
     *
     * @param clazz - класса, являющей поставщиком списка заблокированных логинов - реализующий интерфейс BlackListSupplier
     * @return объект класса
     */
    private BlackListSupplier instantiateClass(Class clazz) {
        if (Arrays.asList(clazz.getInterfaces()).contains(BlackListSupplier.class)) {
            try {
                Constructor<BlackListSupplier> constructor = clazz.getConstructor();
                BlackListSupplier blackListSupplier = constructor.newInstance();
                return blackListSupplier;
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new CreateObjectException("Can't instantiate object from class " + clazz.getName());
            }
        } else {
            throw new CreateObjectException("Class " + clazz.getName() + " do not implement interface BlackListSupplier.class");
        }
    }

    /**
     * Проверяет, что переданный класс имеет тип StarterSession или что в иерархии наследования есть класс с таким типом
     *
     * @param clazz проверяемый класс
     * @return true/false как результат проверки
     */
    private boolean isStarterSession(Class clazz) {
        if (clazz.isPrimitive() || clazz.isInterface()) {
            return false;
        }
        if (clazz == StarterSession.class) {
            return true;
        }
        Class parent = clazz.getSuperclass();
        if (parent == Object.class) {
            return false;
        } else {
            return isStarterSession(parent);
        }
    }

    /**
     * Проверяет, что переданный класс имеет реализацию интерфейса LoginSupplier
     *
     * @param clazz - проверяемый класс
     * @return true/false как результат проверки
     */
    private boolean hasLoginSupplierInterface(Class clazz) {
        if (clazz.isPrimitive()) {
            return false;
        }
        if (clazz.isInterface()) {
            return isLoginSupplier(clazz);
        } else {
            return isClassImplementsLoginSupplier(clazz);
        }
    }

    /**
     * Проверяет что полученный класс интерфейса есть или имеет в иерархии LoginSupplier
     *
     * @param clazz проверяемый класс интерфейса
     * @return true/false как результат проверки
     */
    private boolean isLoginSupplier(Class clazz) {
        if (clazz == LoginSupplier.class) {
            return true;
        }
        Class[] extendInterfaces = clazz.getInterfaces();
        if (extendInterfaces.length == 0) {
            return false;
        }
        return Arrays.stream(extendInterfaces).anyMatch(this::isLoginSupplier);
    }

    private boolean isClassImplementsLoginSupplier(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        if (Arrays.stream(interfaces).anyMatch(this::isLoginSupplier)) {
            return true;
        }
        Class parent = clazz.getSuperclass();
        if (parent == Object.class) {
            return false;
        } else {
            return isClassImplementsLoginSupplier(parent);
        }
    }
}
