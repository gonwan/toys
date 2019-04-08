package com.gonwan.springjpatest;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

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

@EnableJpaAuditing
@SpringBootApplication
public class SpringJpaTestApplication {

    @Bean
    @ConditionalOnProperty(prefix = "application", name = "datasource-proxy-enabled", havingValue = "true")
    public BeanPostProcessor datasourceProxyBeanPostProcessor() {
        return new DatasourceProxyBeanPostProcessor();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaTestApplication.class, args);
    }

}
