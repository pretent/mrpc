package org.pretent.mrpc.support.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ServiceBean
		implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware {

	public void setBeanName(String name) {
		// TODO Auto-generated method stub
		
	}

	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}