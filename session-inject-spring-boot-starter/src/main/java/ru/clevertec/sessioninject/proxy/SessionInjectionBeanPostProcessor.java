package ru.clevertec.sessioninject.proxy;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import ru.clevertec.sessioninject.StarterSession;
import ru.clevertec.sessioninject.exception.CreateObjectException;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.LoginSupplier;
import ru.clevertec.sessioninject.interfaces.SessionInject;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;
import ru.clevertec.sessioninject.util.BlackListForMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class SessionInjectionBeanPostProcessor implements BeanPostProcessor {
    private static Map<String, List<BlackListForMethod>> objectAnnotatedMethod = new HashMap<>();
    private static Map<Class, BlackListSupplier> blackListSupplierObjects = new HashMap<>();
    private SessionSupplier sessionSupplier;
    private BlackListSupplier bannedSupplier;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        List<BlackListForMethod> methodForInjection = Arrays.stream(methods)
                .filter(SessionInjectionBeanPostProcessor::isMethodAnnotatedCorrect)
                .map(SessionInjectionBeanPostProcessor::getBlackListClasses)
                .toList();
        if (methodForInjection.size() > 0) {
            objectAnnotatedMethod.put(beanName, methodForInjection);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (objectAnnotatedMethod.get(beanName) != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setCallback(new SessionInjectProxy(sessionSupplier, objectAnnotatedMethod.get(beanName),
                    bannedSupplier, blackListSupplierObjects));
            bean = enhancer.create();
        }
        return bean;
    }

    /**
     * Проверяет метод на предмет его соответствия требованиям для добавления логики получения Сессии
     * (анотация, в аргументах метода есть Сессии и LoginSupplier)
     * @param method анализируемый метод
     * @return true/false в зависимости от соответствия
     */
    private static boolean isMethodAnnotatedCorrect(Method method) {
        boolean hasSession = false;
        boolean hasLoginSupplier = false;
        if (method.isAnnotationPresent(SessionInject.class)) {
            Class[] parameters = method.getParameterTypes();
            for (Class param : parameters) {
                if (param == StarterSession.class) {
                    hasSession = true;
                    continue;
                }
                if (param == LoginSupplier.class) {
                    hasLoginSupplier = true;
                }
            }
        }
        return hasSession & hasLoginSupplier;
    }

    /**
     * Создать record содержащий имя метода и список классов, поставляющих заблокированные логины, указанный в
     * анотации над методом
     * @param method анализируемый метод
     * @return record с данными
     */

    private static BlackListForMethod getBlackListClasses(Method method) {
        SessionInject annotation = method.getAnnotation(SessionInject.class);
        List<Class> blackListSupplierClasses = Arrays.asList(annotation.blackList());
        blackListSupplierClasses.forEach((it -> {
            if (blackListSupplierObjects.get(it) == null) {
                BlackListSupplier supplier = instantiateClass(it);
                blackListSupplierObjects.put(it, supplier);
            }
        }));
        return new BlackListForMethod(method, blackListSupplierClasses);
    }

    /**
     * Создает объект класса, являющего поставщиком списка заблокированных логинов,
     * @param clazz - класса, являющей поставщиком списка заблокированных логинов - реализующий интерфейс BlackListSupplier
     * @return объект класса
     */
    private static BlackListSupplier instantiateClass(Class clazz) {
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
}
