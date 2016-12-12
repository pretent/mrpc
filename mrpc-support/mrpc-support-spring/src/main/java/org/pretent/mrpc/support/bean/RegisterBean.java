package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.Provider;
import org.pretent.mrpc.annotaion.Service;
import org.pretent.mrpc.provider.mina.MinaProvider;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 */
public class RegisterBean implements BeanPostProcessor, ApplicationContextAware, BeanFactoryPostProcessor {

	private static Logger LOGGER = Logger.getLogger(RegisterBean.class);

	private ApplicationContext applicationContext;

	private Provider provider;

	private ProtocolConfig protocolConfig;

	private String address;

	private InjectBean injectBean = new InjectBean();

	private ExportBean exportBean = new ExportBean();

	private AnnotationConfig annotationBean;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
		try {
			protocolConfig = applicationContext.getBean(ProtocolConfig.class);
		} catch (BeansException e) {
			System.err.println(e.getMessage());
		}
		try {
			annotationBean = applicationContext.getBean(AnnotationConfig.class);
		} catch (BeansException e) {
			System.err.println(e.getMessage());
		}
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * import
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.err.println("00000000000000000000000000:postProcessBeforeInitialization:" + bean.getClass().getName());
		// 处理annotation
		injectBean.inject(bean);
		// export 非service标签
		if (annotationBean != null) {
			String packageName = annotationBean.getPackageName();
			if (bean.getClass().getName().startsWith(packageName) && bean.getClass().getAnnotation(Service.class) != null) {
				export(bean);
			}
		}
		return bean;
	}

	/**
	 * 只export service 标签
	 * export
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 将RootBeanDefinition 为Service的指向真是的bean参考
		System.err.println("11111111111111111111111111111");
		String[] names = beanFactory.getBeanDefinitionNames();
		for (String name : names) {
			if (name.startsWith("mrpc.service:")) {
				AbstractBeanDefinition interfacBean = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
				String ref = (String) interfacBean.getAttribute("ref");
				Object refRealBean = beanFactory.getBean(ref);
				interfacBean.setBeanClass(refRealBean.getClass());
				System.err.println("bean name :" + name + ",ref-->" + ref);
				export(refRealBean);// 导出真实的bean
			}
		}
	}

	public void export(Object bean) {
		if (this.applicationContext != null) {
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