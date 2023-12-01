package ru.clevertec.sessioninject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.sessioninject.interfaces.BlackListSupplier;
import ru.clevertec.sessioninject.interfaces.SessionSupplier;
import ru.clevertec.sessioninject.proxy.SessionInjectionBeanPostProcessor;
import ru.clevertec.sessioninject.util.RestTemplateSessionSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Configuration
@EnableConfigurationProperties(SessionInjectProperties.class)
public class StarterConfiguration {
    @Autowired
    SessionInjectProperties properties;

    //Список заблокированных логинов из файла конфигурации
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
        SessionSupplier sessionSupplier = new RestTemplateSessionSupplier(properties.getUrl());
        return new SessionInjectionBeanPostProcessor(sessionSupplier, blackListSupplier);
    }

}
