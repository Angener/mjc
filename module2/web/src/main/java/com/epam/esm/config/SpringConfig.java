package com.epam.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
@Import(ServiceConfig.class)
public class SpringConfig {

    @Bean
    public ResourceBundleMessageSource getResourceBundleMessageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("/errorMessage");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
