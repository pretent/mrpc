package org.pretent.mrpc.support.handler;

import org.pretent.mrpc.support.bean.AnnotationBean;
import org.pretent.mrpc.support.bean.ReferenceBean;
import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.pretent.mrpc.support.bean.ServiceBean;
import org.pretent.mrpc.support.parer.MrpcBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MrpcNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("register", new MrpcBeanDefinitionParser(RegisterBean.class));
        registerBeanDefinitionParser("annotation", new MrpcBeanDefinitionParser(AnnotationBean.class));
        registerBeanDefinitionParser("service", new MrpcBeanDefinitionParser(ServiceBean.class));
        registerBeanDefinitionParser("reference", new MrpcBeanDefinitionParser(ReferenceBean.class));
        registerBeanDefinitionParser("protocol", new MrpcBeanDefinitionParser(ProtocolConfig.class));
    }
}