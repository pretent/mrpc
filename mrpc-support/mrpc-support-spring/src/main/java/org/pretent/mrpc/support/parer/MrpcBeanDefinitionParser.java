package org.pretent.mrpc.support.parer;

import org.apache.log4j.Logger;
import org.pretent.mrpc.support.bean.ReferenceBean;
import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ServiceConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * AbstractSingleBeanDefinitionParser
 *
 * @author pretent
 */
public class MrpcBeanDefinitionParser implements BeanDefinitionParser {

	private static Logger LOGGER = Logger.getLogger(MrpcBeanDefinitionParser.class);

	private Class<?> clazz;

	public MrpcBeanDefinitionParser(Class<?> clazz) {
		this.clazz = clazz;
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		if (AnnotationConfig.class.equals(clazz)) {
			beanDefinition.setBeanClass(clazz);
			String packageName = element.getAttribute("package");
			beanDefinition.getPropertyValues().add("packageName", packageName);
		}
		if (ProtocolConfig.class.equals(clazz)) {
			beanDefinition.setBeanClass(clazz);
			String host = element.getAttribute("host");
			String port = element.getAttribute("port");
			beanDefinition.getPropertyValues().add("host", host);
			beanDefinition.getPropertyValues().add("port", Integer.parseInt(port));
		}
		if (RegisterBean.class.equals(clazz)) {
			beanDefinition.setBeanClass(clazz);
			String address = element.getAttribute("address");
			beanDefinition.getPropertyValues().add("address", address);
		}
		if (ServiceConfig.class.equals(clazz)) {
			// 是否已经注册了ServiceConfig
			String className = element.getAttribute("interface");
			String ref = element.getAttribute("ref");
			if (!parserContext.getRegistry().containsBeanDefinition(ServiceConfig.class.getName())) {
				RootBeanDefinition serviceBeanDefinition = new RootBeanDefinition();
				serviceBeanDefinition.setBeanClass(ServiceConfig.class);
				serviceBeanDefinition.setLazyInit(false);
				parserContext.getRegistry().registerBeanDefinition(ServiceConfig.class.getName(),
						serviceBeanDefinition);
			}
			// 每一个service标签就是一个需要到处的bean定义
			try {
				Class definationClazz = Class.forName(className);
				beanDefinition.setBeanClass(definationClazz);
				beanDefinition.setAttribute("ref", ref);
				id = "mrpc.service:" + definationClazz.getName();
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		if (ReferenceBean.class.equals(clazz)) {
			String referenceId = element.getAttribute("id");
			String className = element.getAttribute("interface");
			if (referenceId == null) {
				throw new IllegalArgumentException("reference tag  attrubute reference must config");
			}
			// 是否已经注册了ReferenceBean
			if (!parserContext.getRegistry().containsBeanDefinition(ReferenceBean.class.getName())) {
				RootBeanDefinition referenceBeanDefinition = new RootBeanDefinition();
				referenceBeanDefinition.setBeanClass(ReferenceBean.class);
				referenceBeanDefinition.setLazyInit(false);
				referenceBeanDefinition.getPropertyValues().add("id", referenceId);
				referenceBeanDefinition.getPropertyValues().add("interfaceName", className);
				parserContext.getRegistry().registerBeanDefinition(ReferenceBean.class.getName(),
						referenceBeanDefinition);
			}
			// 每一个service标签就是一个需要到处的bean定义
			try {
				Class referenceClass = Class.forName(className);
				beanDefinition.setBeanClass(referenceClass);
				beanDefinition.setAttribute("interfaceName", referenceClass.getName());
				id = "mrpc.reference:" + referenceClass.getName();
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		if ((id == null || id.length() == 0)) {
			String name = element.getAttribute("name");
			if (name == null || name.length() == 0) {
				name = clazz.getName();
			}
			id = name;
		}
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}

	public static void main(String[] args) {
		System.out.println();
	}
}