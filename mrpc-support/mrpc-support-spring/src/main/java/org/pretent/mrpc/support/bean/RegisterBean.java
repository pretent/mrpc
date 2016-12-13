package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.Provider;
import org.pretent.mrpc.annotaion.Service;
import org.pretent.mrpc.client.ProxyFactory;
import org.pretent.mrpc.provider.mina.MinaProvider;
import org.pretent.mrpc.register.ProtocolType;
import org.pretent.mrpc.register.RegisterFactory;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ReferenceConfig;
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
		// annotation @reference
		injectBean.inject(bean);
		// xml <reference>
		return referencce(bean);
	}

	/**
	 * export service
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			annotationConfig = beanFactory.getBean(AnnotationConfig.class);
		} catch (BeansException e) {
			LOGGER.error(e.getMessage());
		}
		String[] names = beanFactory.getBeanDefinitionNames();
		for (String name : names) {
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
							clazz = Class.forName(className);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (className.startsWith(packageName) && clazz != null
								&& clazz.getAnnotation(Service.class) != null) {
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

	private void export(Object bean) {
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