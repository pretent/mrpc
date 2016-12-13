package org.pretent.mrpc.support.spring.boot;

import javax.annotation.PostConstruct;

import org.pretent.mrpc.Provider;
import org.pretent.mrpc.annotaion.Service;
import org.pretent.mrpc.client.ProxyFactory;
import org.pretent.mrpc.provider.mina.MinaProvider;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.support.bean.ExportBean;
import org.pretent.mrpc.support.bean.ImplProxyBean;
import org.pretent.mrpc.support.bean.InjectBean;
import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ReferenceConfig;
import org.pretent.mrpc.support.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

public class MrpcBeanFactory implements BeanFactoryAware, BeanPostProcessor {

	private ConfigurableListableBeanFactory beanFactory;

	private Provider provider;

	private ProtocolConfig protocolConfig;

	private InjectBean injectBean = new InjectBean();

	private ExportBean exportBean = new ExportBean();

	private AnnotationConfig annotationConfig;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
			try {
				annotationConfig = beanFactory.getBean(AnnotationConfig.class);
			} catch (BeansException e) {
				e.printStackTrace();
			}
			// this.configure();
		}
	}

	@PostConstruct
	public void configure() {
		String[] names = beanFactory.getBeanDefinitionNames();
		for (String name : names) {
			// System.out.println("55555555555555555555555555555555:->"+name);
			if (!RegisterBean.class.getName().equals(name) && !ReferenceConfig.class.getName().equals(name)
					&& !AnnotationConfig.class.getName().equals(name) && !ProtocolConfig.class.getName().equals(name)
					&& !ServiceConfig.class.getName().equals(name)) {
				if (name.startsWith("mrpc.service:")) {
					// xml <service> export
					AbstractBeanDefinition interfaceBean = (AbstractBeanDefinition) beanFactory.getBeanDefinition(name);
					String ref = (String) interfaceBean.getAttribute("ref");
					Object refRealBean = beanFactory.getBean(ref);
					interfaceBean.setBeanClass(refRealBean.getClass());
					export(refRealBean);
				} else {
					// annotation @service export
					if (annotationConfig != null) {
						String packageName = annotationConfig.getPackageName();
						AbstractBeanDefinition interfaceBean = (AbstractBeanDefinition) beanFactory
								.getBeanDefinition(name);
						String className = interfaceBean.getBeanClassName();
						Class clazz = null;
						try {
							if (className != null) {
								clazz = Class.forName(className);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (className != null && packageName != null && className.startsWith(packageName)
								&& clazz != null && clazz.getAnnotation(Service.class) != null) {
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

	private void export(Object bean) {
		try {
			if (provider == null) {
				provider = new MinaProvider();
				if (protocolConfig != null) {
					provider.setHost(protocolConfig.getHost());
					provider.setPort(protocolConfig.getPort());
				}
				String address = "zookeeper://127.0.0.1:2181";
				provider.setRegister(new RegisterFactory().getRegister(address,
						ProtocolType.valueOf(address.substring(0, address.indexOf("/") - 1).toUpperCase())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		exportBean.export(provider, bean);
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.configure();
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// annotation @reference
		injectBean.inject(bean);
		// xml <reference>
		return referencce(bean);
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	private Object referencce(Object bean) {
		Object object = null;
		if (ImplProxyBean.class.equals(bean.getClass())) {
			ImplProxyBean impl = (ImplProxyBean) bean;
			String id = impl.getId();
			String interfaceName = impl.getInterfaceName();
			try {
				Class<?> clazz = Class.forName(interfaceName);
				object = ProxyFactory.getService(clazz);
				return object;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bean;
	}

}
