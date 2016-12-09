package org.pretent.mrpc.support.handler;

import org.pretent.mrpc.support.bean.AnnotationBean;
import org.pretent.mrpc.support.bean.ReferenceBean;
import org.pretent.mrpc.support.bean.ServiceBean;
import org.pretent.mrpc.support.parer.MrpcBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MrpcNamespaceHandler extends NamespaceHandlerSupport {
	public void init() {
		registerBeanDefinitionParser("service", new MrpcBeanDefinitionParser(ServiceBean.class));
		registerBeanDefinitionParser("reference", new MrpcBeanDefinitionParser(ReferenceBean.class));
		registerBeanDefinitionParser("annotation", new MrpcBeanDefinitionParser(AnnotationBean.class));
	}
}