package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
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

public class AnnotationBean
        implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private static Logger LOGGER = Logger.getLogger(AnnotationBean.class);

    private String packageName;

    private ApplicationContext applicationContext;

    private RegisterBean registerBean;

    private ServiceBean serviceBean;

    private ProtocolConfig protocolConfig;

    private Provider provider;

    private InjectBean injectBean = new InjectBean();

    private ExportBean exportBean = new ExportBean();

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        try {
            registerBean = applicationContext.getBean(RegisterBean.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
        try {
            serviceBean = applicationContext.getBean(ServiceBean.class);
        } catch (BeansException e) {
            System.err.println(e.getMessage());
        }
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
        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHH" + bean.getClass().getName());
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("FFFFFFFFFFFFFFFFFFFFFFFF" + bean.getClass().getName());
        // 只有配置了bean才会进入,每个bean都会进入
        // 处理bean(reference)初始化之前设置标有（reference ）属性或者方法
        injectBean.inject(bean);

        // 处理reference标签操作

        // 处理service标签操作

        // 处理export操作
        if (this.applicationContext != null) {
            AnnotationBean annotationBean = applicationContext.getBean(AnnotationBean.class);
            this.packageName = annotationBean.packageName;
            if (!isMatchPackage(bean)) {
                return bean;
            }
            Service service = bean.getClass().getAnnotation(Service.class);
            if (service != null) {
                try {
                    if (serviceBean != null) {
                        provider = serviceBean.getProvider();
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

    public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
        // TODO Auto-generated method stub

    }

    public void destroy() throws Exception {
        // 取消bean注册
        // 断开连接等
    }

    private boolean isMatchPackage(Object bean) {
        if (packageName == null || packageName.length() == 0) {
            return true;
        }
        String beanClassName = bean.getClass().getName();
        if (beanClassName.startsWith(packageName)) {
            return true;
        }
        return false;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}