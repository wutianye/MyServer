package com.example.demo.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/* *
 *
 * 功能描述 获取bean对象:
 *
 * @param:
 * @return:
 * @auther: liuyunxing
 * @Description //TODO
 * @date: 2018/7/14 9:50
 */
@Component
public class SpringBeanFactoryUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBeanFactoryUtil.applicationContext == null) {
            SpringBeanFactoryUtil.applicationContext = applicationContext;
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
