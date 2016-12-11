package org.pretent.mrpc.support.bean;

import org.pretent.mrpc.Provider;
import org.pretent.mrpc.annotaion.Service;
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

public class ServiceBean
        implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private String interfaceName;

    private String ref;

    private ApplicationContext applicationContext;

    private Provider provider;

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
        // 处理export操作
        if (this.applicationContext != null) {
            AnnotationBean annotationBean = applicationContext.getBean(AnnotationBean.class);
            Service service = bean.getClass().getAnnotation(Service.class);
            if (service != null) {
                try {
                    if (annotationBean != null) {
                        provider = annotationBean.getProvider();
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
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return null;
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
    }
}