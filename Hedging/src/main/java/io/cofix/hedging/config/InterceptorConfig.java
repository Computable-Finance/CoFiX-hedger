package io.cofix.hedging.config;

import io.cofix.hedging.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将我们上步定义的实现了HandlerInterceptor接口的拦截器实例authenticationInterceptor添加InterceptorRegistration中，并设置过滤规则，所有请求都要经过authenticationInterceptor拦截。
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**");
    }
}
