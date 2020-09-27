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
        /**
         *  Will we realized HandlerInterceptor interfaces defined shangbu interceptor instance authenticationInterceptor added InterceptorRegistration,
         *  and set up the filtering rules, all request must pass authenticationInterceptor intercept.
         */
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**");
    }
}
