package com.lolkt.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringUtils implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<ApplicationEvent>, DisposableBean {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return applicationContext != null ? applicationContext.getEnvironment() : null;
    }

    public static Object getBean(String name) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(name);
            } catch (Exception var2) {
                log.error("getBean not found: " + var2.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(clazz);
            } catch (Exception var2) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(name, clazz);
            } catch (Exception var3) {
                log.error("getBean not found: " + var3.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void destroy() throws Exception {
        applicationContext = null;
    }


    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = configurableApplicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent.getSource() instanceof ConfigurableApplicationContext) {
            this.initialize((ConfigurableApplicationContext) applicationEvent.getSource());
        }
    }
}