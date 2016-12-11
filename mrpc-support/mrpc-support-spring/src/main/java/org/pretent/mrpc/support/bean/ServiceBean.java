package org.pretent.mrpc.support.bean;

import org.pretent.mrpc.Provider;
import org.pretent.mrpc.provider.mina.MinaProvider;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 服务注册
 */
public class ServiceBean
        implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private String interfaceName;

    private String ref;

    private ApplicationContext applicationContext;

    private Provider provider;

    private AnnotationBean annotationBean;

    private RegisterBean registerBean;

    private ProtocolConfig protocolConfig;

    private ExportBean exportBean = new ExportBean();

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void destroy() throws Exception {

    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 处理export操作
        try {
            if (annotationBean != null) {
                // 直接让annotation处理
                return bean;
            }
            if (provider == null) {
                provider = new MinaProvider();
                if (protocolConfig != null) {
                    provider.setHost(protocolConfig.getHost());
                    provider.setPort(protocolConfig.getPort());
                }
                if (registerBean != null) {
                    provider.setRegister(new RegisterFactory().getRegister(registerBean.getAddress(), ProtocolType.valueOf(registerBean.getAddress().substring(0, registerBean.getAddress().indexOf("/") - 1).toUpperCase())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        exportBean.export(provider, bean);
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        try {
            protocolConfig = applicationContext.getBean(ProtocolConfig.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
        try {
            registerBean = applicationContext.getBean(RegisterBean.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
        try {
            annotationBean = applicationContext.getBean(AnnotationBean.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}