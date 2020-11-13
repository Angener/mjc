package com.epam.esm.config;

import com.epam.esm.dao.DatabaseResolver;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
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
@PropertySource("classpath:${spring.profiles.active}.properties")
@ComponentScan(basePackages = {"com.epam.esm.dao"})
@EnableTransactionManagement
public class JdbcConfig {

    @Value("${postgres.driverClass}")
    private String driverClass;
    @Value("${postgres.url}")
    private String databaseUrl;
    @Value("${postgres.databaseName}")
    private String databaseName;
    @Value("${postgres.username}")
    private String username;
    @Value("${postgres.password}")
    private String password;
    @Value("${postgres.maxPoolSize}")
    private int maxPoolSize;

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate((jdbcTemplate));
    }

    @Bean
    public SimpleJdbcInsert getTagGiftCertificateJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcInsert(jdbcTemplate).withTableName("tag_gift_certificate");
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

    @Bean("giftCertificateDatabaseResolver")
    public DatabaseResolver<GiftCertificate> getGiftCertificateTableDatabaseResolver(JdbcTemplate jdbcTemplate,
                                                                 NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        return new DatabaseResolver<>(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Bean("tagDatabaseResolver")
    public DatabaseResolver<Tag> getTagTableDatabaseResolver(JdbcTemplate jdbcTemplate,
                                                     NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        return new DatabaseResolver<>(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
