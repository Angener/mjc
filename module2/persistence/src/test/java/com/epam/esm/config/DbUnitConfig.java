package com.epam.esm.config;

import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.Properties;

@SpringJUnitConfig(JdbcConfig.class)
public class DbUnitConfig extends DBTestCase {
    protected IDatabaseTester tester;
    protected IDataSet beforeData;
    private final Properties properties;

    @BeforeEach
    public void setUp() throws Exception {
        tester = new JdbcDatabaseTester(properties.getProperty("postgres.driverClass"),
                properties.getProperty("postgres.url"),
                properties.getProperty("postgres.username"),
                properties.getProperty("postgres.password"));
    }

    public DbUnitConfig() {
        properties = new Properties();
        loadProperties(properties);
        setSystemProperties(properties);
    }

    private void loadProperties(Properties properties) {
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("postgres.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setSystemProperties(Properties properties) {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
                properties.getProperty("postgres.driverClass"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
                properties.getProperty("postgres.url"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
                properties.getProperty("postgres.username"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
                properties.getProperty("postgres.password"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "giftCertificate");
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return beforeData;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }
}
