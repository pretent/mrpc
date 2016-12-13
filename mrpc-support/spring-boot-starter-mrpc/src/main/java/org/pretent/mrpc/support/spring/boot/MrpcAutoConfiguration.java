package org.pretent.mrpc.support.spring.boot;

import org.pretent.mrpc.support.config.AnnotationConfig;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureOrder(value = 1)
public class MrpcAutoConfiguration {

	public MrpcAutoConfiguration() {
		System.err.println("MrpcAutoConfiguration init...");
	}

	@Bean
	public AnnotationConfig mAnnotationConfig() {
		AnnotationConfig annotationConfig = new AnnotationConfig();
		annotationConfig.setPackageName("org.pretent.service.impl");
		return annotationConfig;
	}

	@Bean
	public MrpcBeanFactory mMrpcBeanFactory() {
		return new MrpcBeanFactory();
	}
}
