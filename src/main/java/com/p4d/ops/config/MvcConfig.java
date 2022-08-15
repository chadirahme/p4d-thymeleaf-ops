package com.p4d.ops.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("welcome");
        //registry.addViewController("/index").setViewName("welcome");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/403").setViewName("/error/403");
    }
}