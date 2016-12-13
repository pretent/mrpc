package org.pretent.mrpc.support.parer;

import org.pretent.mrpc.support.bean.ImplProxyBean;
import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ReferenceConfig;
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
			String className = element.getAttribute("interface");
			String ref = element.getAttribute("ref");
			if (!parserContext.getRegistry().containsBeanDefinition(ServiceConfig.class.getName())) {
				RootBeanDefinition serviceBeanDefinition = new RootBeanDefinition();
				serviceBeanDefinition.setBeanClass(ServiceConfig.class);
				serviceBeanDefinition.setLazyInit(false);
				parserContext.getRegistry().registerBeanDefinition(ServiceConfig.class.getName(),
						serviceBeanDefinition);
			}
			try {
				Class definationClazz = Class.forName(className);
				beanDefinition.setBeanClass(definationClazz);
				beanDefinition.setAttribute("ref", ref);
				id = "mrpc.service:" + definationClazz.getName();
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		if (ReferenceConfig.class.equals(clazz)) {
			String referenceId = element.getAttribute("id");
			String className = element.getAttribute("interface");
			if (referenceId == null) {
				throw new IllegalArgumentException("reference tag  attrubute reference must config");
			}
			if (!parserContext.getRegistry().containsBeanDefinition(ReferenceConfig.class.getName())) {
				RootBeanDefinition referenceBeanDefinition = new RootBeanDefinition();
				referenceBeanDefinition.setBeanClass(ReferenceConfig.class);
				referenceBeanDefinition.setLazyInit(false);
				parserContext.getRegistry().registerBeanDefinition(ReferenceConfig.class.getName(),
						referenceBeanDefinition);
			}
			try {
				Class referenceClass = Class.forName(className);
				beanDefinition.setBeanClass(ImplProxyBean.class);
				beanDefinition.getPropertyValues().add("id", referenceId);
				beanDefinition.getPropertyValues().add("interfaceName", referenceClass.getName());
				// id = "mrpc.reference:" + referenceClass.getName();
				id = referenceId;
				if (id == null) {
					throw new IllegalArgumentException("attribute id must be set of reference tag");
				}
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