package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.Provider;
import org.pretent.mrpc.annotaion.Service;
import org.pretent.mrpc.provider.mina.MinaProvider;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 */
public class RegisterBean implements BeanPostProcessor, BeanFactoryPostProcessor {

    private static Logger LOGGER = Logger.getLogger(RegisterBean.class);

    private Provider provider;

    private ProtocolConfig protocolConfig;

    private String address;

    private InjectBean injectBean = new InjectBean();

    private ExportBean exportBean = new ExportBean();

    private AnnotationConfig annotationConfig;

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * import
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.err.println("22222222222222222222222222:postProcessBeforeInitialization:" + bean.getClass().getName());
        // 处理annotation
        injectBean.inject(bean);
        return bean;
    }

    /**
     * 只export service 标签
     * export
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 将RootBeanDefinition 为Service的指向真是的bean参考
        System.err.println("11111111111111111111111111111");
        try {
            annotationConfig = beanFactory.getBean(AnnotationConfig.class);
        } catch (BeansException e) {
            // e.printStackTrace();
        }
        String[] names = beanFactory.getBeanDefinitionNames();
        for (String name : names) {
            // 非配置bean
            if (!RegisterBean.class.getName().equals(name) && !RegisterBean.class.getName().equals(name)
                    && !AnnotationConfig.class.getName().equals(name)
                    && !ProtocolConfig.class.getName().equals(name)
                    && !ServiceConfig.class.getName().equals(name)) {
                // service 标签配置的bean 无条件导出服务
                if (name.startsWith("mrpc.service:")) {
                    AbstractBeanDefinition interfaceBean = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
                    String ref = (String) interfaceBean.getAttribute("ref");
                    Object refRealBean = beanFactory.getBean(ref);
                    interfaceBean.setBeanClass(refRealBean.getClass());
                    System.err.println("bean name :" + name + ",ref-->" + ref);
                    export(refRealBean);// 导出真实的bean
                } else {
                    // export 非service标签
                    // service bean 可以发布服务
                    // annotation bean 可以发布服务
                    // 只有bean标签是不能发布服务的
                    if (annotationConfig != null) {
                        String packageName = annotationConfig.getPackageName();
                        AbstractBeanDefinition interfaceBean = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
                        String className = interfaceBean.getBeanClassName();
                        Class clazz = null;
                        try {
                            clazz = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (className.startsWith(packageName) && clazz != null && clazz.getAnnotation(Service.class) != null) {
                            try {
                                export(clazz.newInstance());
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }
    }

    public void export(Object bean) {
        try {
            if (provider == null) {
                provider = new MinaProvider();
                if (protocolConfig != null) {
                    provider.setHost(protocolConfig.getHost());
                    provider.setPort(protocolConfig.getPort());
                }
                provider.setRegister(new RegisterFactory().getRegister(address,
                        ProtocolType.valueOf(address.substring(0, address.indexOf("/") - 1).toUpperCase())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        exportBean.export(provider, bean);
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}