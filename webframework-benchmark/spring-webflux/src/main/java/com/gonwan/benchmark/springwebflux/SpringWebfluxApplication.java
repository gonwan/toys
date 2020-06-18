package com.gonwan.benchmark.springwebflux;

import io.netty.channel.ChannelOption;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.netty.resources.LoopResources;

import javax.sql.DataSource;
import java.lang.reflect.Method;

//@Component
class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object ob = bean;
        if (bean instanceof Advised) {
            ob = ((Advised) bean).getTargetSource();
        }
        if (ob instanceof DataSource) {
            ProxyFactory factory = new ProxyFactory(bean);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
            return factory.getProxy();
        }
        return bean;
    }

    private static class ProxyDataSourceInterceptor implements MethodInterceptor {

        private final DataSource dataSource;

        public ProxyDataSourceInterceptor(DataSource dataSource) {
            this.dataSource = ProxyDataSourceBuilder.create(dataSource).logQueryBySlf4j(SLF4JLogLevel.INFO).build();
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
            if (proxyMethod != null) {
                return proxyMethod.invoke(dataSource, invocation.getArguments());
            }
            return invocation.proceed();
        }
    }

}

@EnableR2dbcRepositories
@SpringBootApplication
public class SpringWebfluxApplication {

    /*
     * https://www.baeldung.com/spring-boot-reactor-netty
     */
    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return server ->
                server.tcpConfiguration(tcpServer ->
                        tcpServer.runOn(LoopResources.create("reactor-netty", 1, LoopResources.DEFAULT_IO_WORKER_COUNT, true))
                                .selectorOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                );
    }

    /*
     * The default implementation of ReactorResourceFactory create a HttpResources, and start the server, override it.
     */
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
        webServerFactory.addServerCustomizers(nettyServerCustomizer());
        return webServerFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxApplication.class, args);
    }

}
