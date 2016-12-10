package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.annotaion.Reference;
import org.pretent.mrpc.annotaion.Service;
import org.pretent.mrpc.client.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationBean
        implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

    private static Logger LOGGER = Logger.getLogger(AnnotationBean.class);

    private String packageName;

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // bean初始化之后发布服务（annotation配置的包内标有Service注解的bean）
        if (this.applicationContext != null) {
            AnnotationBean annotationBean = applicationContext.getBean(AnnotationBean.class);
            this.packageName = annotationBean.packageName;
            if (!isMatchPackage(bean)) {
                return bean;
            }
            Service service = bean.getClass().getAnnotation(Service.class);
            if (service != null) {
                // TODO export bean to register
                LOGGER.debug("================" + bean.getClass().getName() + "需要发布bena到注册中心");
            }
        }
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // bean初始化之前设置标有（reference ）属性或者方法
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.getType().isPrimitive()) {
                Reference reference = field.getAnnotation(Reference.class);
                if (reference != null) {
                    LOGGER.debug("field================" + bean.getClass().getName() + "--->" + field.getName() + "需要生成reference的代理对象");
                    try {
                        Object value = ProxyFactory.getService(field.getType());
                        if (value != null) {
                            field.setAccessible(true);
                            field.set(bean, value);
                            field.setAccessible(false);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            Reference reference = method.getAnnotation(Reference.class);
            if (reference != null) {
                LOGGER.debug("method================" + bean.getClass().getName() + "--->" + method.getName() + "需要生成reference的代理对象");
                try {
                    Object value = ProxyFactory.getService(method.getParameterTypes()[0]);
                    if (value != null) {
                        method.invoke(bean, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


}