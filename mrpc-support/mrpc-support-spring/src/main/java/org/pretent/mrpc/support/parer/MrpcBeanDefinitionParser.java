package org.pretent.mrpc.support.parer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * AbstractSingleBeanDefinitionParser
 * 
 * @author pretent
 *
 */
public class MrpcBeanDefinitionParser implements BeanDefinitionParser {

	private Class<?> clazz;

	public MrpcBeanDefinitionParser(Class<?> clazz) {
		this.clazz = clazz;
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(this.clazz);
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		if ((id == null || id.length() == 0)) {
			String generatedBeanName = element.getAttribute("name");
			if (generatedBeanName == null || generatedBeanName.length() == 0) {
				generatedBeanName = clazz.getName();
			}
			id = generatedBeanName;
		}
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		return beanDefinition;
	}
}