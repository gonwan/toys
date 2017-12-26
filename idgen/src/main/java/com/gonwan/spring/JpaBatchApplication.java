package com.gonwan.spring;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/*
 * See: http://blog.arnoldgalovics.com/2017/06/26/configuring-a-datasource-proxy-in-spring-boot/
 */
class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            DataSource dataSourceBean = (DataSource) bean;
            return ProxyDataSourceBuilder.create(dataSourceBean).logQueryBySlf4j(SLF4JLogLevel.INFO).build();
        }
        return bean;
    }

}

@SpringBootApplication
public class JpaBatchApplication {

    //@Bean
    public BeanPostProcessor datasourceProxyBeanPostProcessor() {
        return new DatasourceProxyBeanPostProcessor();
    }

}
