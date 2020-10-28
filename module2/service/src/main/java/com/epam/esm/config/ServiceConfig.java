package com.epam.esm.config;

import com.epam.esm.entity.Tag;
import com.epam.esm.service.GiftCertificateServiceImpl;
import com.epam.esm.service.TagServiceImpl;
import com.epam.esm.util.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(JdbcConfig.class)
@ComponentScan(basePackageClasses = {TagServiceImpl.class, GiftCertificateServiceImpl.class, BeanFactory.class})
public class ServiceConfig {
    @Bean
    BeanFactory getBeanFactory(){
        return new BeanFactory();
    }

    @Bean
    @Scope("prototype")
    Tag getTag(String name) {
        return new Tag(name);
    }
}
