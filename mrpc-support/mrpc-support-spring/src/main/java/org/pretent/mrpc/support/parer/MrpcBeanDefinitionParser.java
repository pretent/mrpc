package org.pretent.mrpc.support.parer;

import org.pretent.mrpc.support.bean.AnnotationBean;
import org.pretent.mrpc.support.bean.ReferenceBean;
import org.pretent.mrpc.support.bean.ServiceBean;
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
		if(clazz.equals(AnnotationBean.class)){
			String packageName = element.getAttribute("package");
			System.out.println("-========================"+packageName);
			beanDefinition.setAttribute("packageName",packageName);
		}
		String id = element.getAttribute("id");
		if(clazz.equals(ServiceBean.class)){
		}
		if(clazz.equals(ReferenceBean.class)){

		}
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

	public static void main(String[] args) {
		System.out.println();
	}
}