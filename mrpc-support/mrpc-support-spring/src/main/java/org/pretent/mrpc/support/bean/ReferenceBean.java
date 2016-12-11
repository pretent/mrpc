package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.client.ProxyFactory;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 */
public class ReferenceBean
        implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private static Logger LOGGER = Logger.getLogger(RegisterBean.class);

    private ApplicationContext applicationContext;

    private ProtocolConfig protocolConfig;

    private String interfaceName;

    private InjectBean injectBean = new InjectBean();

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        try {
            protocolConfig = applicationContext.getBean(ProtocolConfig.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // bean初始化之后发布服务（annotation配置的包内标有Service注解的bean）
        // 只有配置了bean才会进入,每个bean都会进入
        // 所以的bean加了Service注解都会被发布
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 为配置的接口生成代理对象
        Object object = null;
        try {
            Class<?> clazz = Class.forName(interfaceName);
            object = ProxyFactory.getService(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
    }
}