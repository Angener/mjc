package com.epam.esm.config;

import com.epam.esm.service.GiftCertificateServiceImpl;
import com.epam.esm.service.TagServiceImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(JdbcConfig.class)
@ComponentScan(basePackageClasses = {TagServiceImpl.class, GiftCertificateServiceImpl.class})
@EnableTransactionManagement
public class ServiceConfig {
}
