package org.pretent.mrpc.support.handler;

import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.config.ReferenceConfig;
import org.pretent.mrpc.support.config.ServiceConfig;
import org.pretent.mrpc.support.parer.MrpcBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MrpcNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("register", new MrpcBeanDefinitionParser(RegisterBean.class));
        registerBeanDefinitionParser("annotation", new MrpcBeanDefinitionParser(AnnotationConfig.class));
        registerBeanDefinitionParser("service", new MrpcBeanDefinitionParser(ServiceConfig.class));
        registerBeanDefinitionParser("reference", new MrpcBeanDefinitionParser(ReferenceConfig.class));
        registerBeanDefinitionParser("protocol", new MrpcBeanDefinitionParser(ProtocolConfig.class));
    }
}