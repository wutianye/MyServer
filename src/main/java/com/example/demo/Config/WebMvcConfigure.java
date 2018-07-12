package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigure extends  WebMvcConfigurerAdapter{

    @Bean
    public HandlerInterceptor getMyInterceptor(){
        return new AuthenticationInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMyInterceptor()).addPathPatterns("/user/**");
        super.addInterceptors(registry);
    }

    @Bean
    public com.example.demo.Config.CurrentUseridMethodArgumentResolver currentUseridMethodArgumentResolver() {
        return  new com.example.demo.Config.CurrentUseridMethodArgumentResolver();
    }
}
