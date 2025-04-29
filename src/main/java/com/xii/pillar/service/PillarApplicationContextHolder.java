package com.xii.pillar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PillarApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("PillarApplicationContextHolder ...");
        PillarApplicationContextHolder.applicationContext = applicationContext;
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> t) {
        return applicationContext.getBeansOfType(t);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

}
