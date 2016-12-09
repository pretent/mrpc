package org.pretent.mrpc.support.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AnnotationBean
		implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// bean初始化之后发布服务（标有）
		//  Service service = bean.getClass().getAnnotation(Service.class);
		System.out.println("postProcessAfterInitialization-->"+bean.getClass().getName()+":::::"+beanName);
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInitialization-->"+bean.getClass().getName()+":::::"+beanName);
		// bean初始化之前设置标有（reference ）属性或者方法
		/**
		Reference reference = field.getAnnotation(Reference.class);
      	if (reference != null) {
              Object value = refer(reference, field.getType());
              if (value != null) {
              	field.set(bean, value);
              }
      	}
      	
      	// 这是set方法
      	Reference reference = method.getAnnotation(Reference.class);
                	if (reference != null) {
	                	Object value = refer(reference, method.getParameterTypes()[0]);
	                	if (value != null) {
	                		method.invoke(bean, new Object[] {  });
	                	}
                	}
      	**/
		return bean;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
		// TODO Auto-generated method stub

	}

	public void destroy() throws Exception {
		// 取消bean注册
		// 断开连接等
	}

}