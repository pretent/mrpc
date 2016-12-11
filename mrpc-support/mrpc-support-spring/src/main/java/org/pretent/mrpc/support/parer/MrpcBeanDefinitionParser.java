package org.pretent.mrpc.support.parer;

import org.apache.log4j.Logger;
import org.pretent.mrpc.support.bean.AnnotationBean;
import org.pretent.mrpc.support.bean.ReferenceBean;
import org.pretent.mrpc.support.bean.RegisterBean;
import org.pretent.mrpc.support.bean.ServiceBean;
import org.pretent.mrpc.support.config.ProtocolConfig;
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

    private static Logger LOGGER = Logger.getLogger(AnnotationBean.class);

    private Class<?> clazz;

    public MrpcBeanDefinitionParser(Class<?> clazz) {
        this.clazz = clazz;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setLazyInit(false);
        if (clazz.equals(AnnotationBean.class)) {
            String packageName = element.getAttribute("package");
            beanDefinition.getPropertyValues().add("packageName", packageName);
        }
        if (ProtocolConfig.class.equals(clazz)) {
            String host = element.getAttribute("host");
            String port = element.getAttribute("port");
            beanDefinition.getPropertyValues().add("host", host);
            beanDefinition.getPropertyValues().add("port", Integer.parseInt(port));
        }
        if (RegisterBean.class.equals(clazz)) {
            String address = element.getAttribute("address");
            beanDefinition.setAttribute("address", address);
            beanDefinition.getPropertyValues().add("address", address);
        }
        if (ServiceBean.class.equals(clazz)) {
            // ServiceBean 本身
            String service = element.getAttribute("interface");
            String ref = element.getAttribute("ref");
            beanDefinition.getPropertyValues().add("interfaceName", service);
            beanDefinition.getPropertyValues().add("ref", ref);


            /** **/
            // 每一个标签都是需要发布一个服务
            RootBeanDefinition bean = new RootBeanDefinition();
            try {
                bean.setBeanClass(Class.forName(service));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            bean.setLazyInit(false);
            // parserContext.getRegistry().registerBeanDefinition(service, bean);
            /** **/
        }
        if (ReferenceBean.class.equals(clazz)) {
            String service = element.getAttribute("interface");
            beanDefinition.getPropertyValues().add("interfaceName", service);

            /** **/
            // 每一个标签都是需要发布一个服务
            RootBeanDefinition bean = new RootBeanDefinition();
            try {
                bean.setBeanClass(Class.forName(service));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            bean.setLazyInit(false);
            // parserContext.getRegistry().registerBeanDefinition(service, bean);
            /** **/
        }

        String id = element.getAttribute("id");
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