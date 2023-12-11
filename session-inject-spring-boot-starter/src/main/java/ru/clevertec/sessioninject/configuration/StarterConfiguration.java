package ru.clevertec.sessioninject.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;
import ru.clevertec.sessioninject.property.SessionInjectProperties;
import ru.clevertec.sessioninject.proxy.SessionInjectionBeanPostProcessor;
import ru.clevertec.sessioninject.service.RestTemplateSessionSupplier;

import java.util.*;


@Configuration
@AllArgsConstructor
@Import({RestTemplateSessionSupplier.class})
@EnableConfigurationProperties(SessionInjectProperties.class)
public class StarterConfiguration {

    private SessionInjectProperties properties;
    private SessionSupplier sessionSupplier;
    private List<BlackListSupplier> blackListSupplierBeans;

    @Bean
    public BlackListSupplier blackListSupplier() {
        List<String> bannedListInConfig = new ArrayList<>();
        if (Objects.nonNull(properties.getBannedList())) {
            bannedListInConfig.addAll(properties.getBannedList());
        }
        return () -> bannedListInConfig;
    }

    @Bean
    public SessionInjectionBeanPostProcessor addProxySessionInject(BlackListSupplier blackListSupplier) {
        Map<Class, BlackListSupplier> blackListSupplierMatches = mapBlackListSupplierBean();
        return new SessionInjectionBeanPostProcessor(blackListSupplierMatches, sessionSupplier, blackListSupplier);
    }

    private Map<Class, BlackListSupplier> mapBlackListSupplierBean() {
        Map<Class, BlackListSupplier> blackListSupplierMatches = new HashMap<>();
        for (BlackListSupplier supplier : blackListSupplierBeans) {
            Class clazz = ClassUtils.getUserClass(supplier);
            blackListSupplierMatches.put(clazz, supplier);
        }
        return blackListSupplierMatches;
    }

}
