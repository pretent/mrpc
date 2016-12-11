package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 使用mrpc这个标签必须配置
 * 处理属性和方法上的@Reference注解,为bean注入代理对象
 * <p>
 * <mrpc:register/> 只能为消费者
 * <mrpc:reference/> 只能为消费者
 * <mrpc:service/> 只能为提供者
 * <p>
 * <mrpc:annotation/> 提供者或者消费者
 * <mrpc:annotation/> <mrpc:register/>提供者或者消费者
 */
public class RegisterBean
        implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private static Logger LOGGER = Logger.getLogger(RegisterBean.class);

    private ApplicationContext applicationContext;

    private AnnotationBean annotationBean;

    private ProtocolConfig protocolConfig;

    private String address;

    private InjectBean injectBean = new InjectBean();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        try {
            protocolConfig = applicationContext.getBean(ProtocolConfig.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
        try {
            annotationBean = applicationContext.getBean(AnnotationBean.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // bean初始化之后发布服务（annotation配置的包内标有Service注解的bean）
        // 只有配置了bean才会进入,每个bean都会进入
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (annotationBean != null) {
            // annotationBean 去操作
            return bean;
        }
        injectBean.inject(bean);
        return bean;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}