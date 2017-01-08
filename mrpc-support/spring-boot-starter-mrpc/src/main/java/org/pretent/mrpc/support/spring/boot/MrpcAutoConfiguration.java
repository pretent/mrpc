package org.pretent.mrpc.support.spring.boot;

import org.pretent.mrpc.support.config.AnnotationConfig;
import org.pretent.mrpc.support.config.ProtocolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 支持spring boot的自动配置
 */
@Configuration
@PropertySource("application.properties")
@EnableConfigurationProperties(value = {org.pretent.mrpc.support.spring.boot.config.AnnotationConfig.class, org.pretent.mrpc.support.spring.boot.config.ProtocolConfig.class})
public class MrpcAutoConfiguration {

    @Value("${mrpc.register:zookeeper://127.0.0.1:2181}")
    private String address;

    @Autowired(required = false)
    private org.pretent.mrpc.support.spring.boot.config.AnnotationConfig aconfig;

    @Autowired(required = false)
    private org.pretent.mrpc.support.spring.boot.config.ProtocolConfig pconfig;

    public MrpcAutoConfiguration() {
        System.err.println("MrpcAutoConfiguration init...");
    }

    @Bean
    @ConditionalOnProperty("mrpc.packageName")
    public AnnotationConfig mAnnotationConfig() {
        AnnotationConfig annotationConfig = new AnnotationConfig();
        annotationConfig.setPackageName(aconfig.getPackageName());
        return annotationConfig;
    }

    @Bean
    @ConditionalOnProperty("mrpc.host")
    public ProtocolConfig mProtocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setHost(pconfig.getHost());
        protocolConfig.setPort(pconfig.getPort());
        return protocolConfig;
    }

    @Bean
    public MrpcBootRegisterBean mMrpcBeanFactory() {
        MrpcBootRegisterBean mrpcBeanFactory = new MrpcBootRegisterBean();
        System.err.println(address);
        mrpcBeanFactory.setAddress(address);
        return mrpcBeanFactory;
    }
}
