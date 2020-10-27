package com.epam.esm.config;

import com.epam.esm.dao.GiftCertificateDaoImpl;
import com.epam.esm.dao.TagDaoImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:postgres.properties")
@ComponentScan(basePackageClasses = {JdbcConfig.class, TagDaoImpl.class, GiftCertificateDaoImpl.class})
@EnableTransactionManagement
public class JdbcConfig {

    @Value("${postgres.driverClass}") private String driverClass;
    @Value("${postgres.url}") private String databaseUrl;
    @Value("${postgres.databaseName}") private String databaseName;
    @Value("${postgres.username}") private String username;
    @Value("${postgres.password}") private String password;
    @Value("${postgres.maxPoolSize}") private int maxPoolSize;

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate((jdbcTemplate));
    }

    @Bean("tag")
    public SimpleJdbcInsert getTagTableSimpleJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcInsert(jdbcTemplate).withTableName("tag");
    }

    @Bean("tag_giftCertificate")
    public SimpleJdbcInsert getTag_GiftCertificateJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcInsert(jdbcTemplate).withTableName("tag_giftCertificate");
    }

    @Bean
    public DataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }

    @Bean
    public HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClass);
        config.setJdbcUrl(databaseUrl);
        config.setPoolName(databaseName);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        return config;
    }

    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
